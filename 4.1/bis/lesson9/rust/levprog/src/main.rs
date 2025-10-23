use sha2::{Digest, Sha256};
use std::env;
use std::fs;
use std::path::PathBuf;
use std::process::exit;
use winreg::enums::HKEY_LOCAL_MACHINE;
use winreg::RegKey;

const EXPECTED_MACHINE_GUID_HASH: &str =
    "a0b9577f2809fdc013592d41eb469319b0bf27712db8c615069013862645f4d9";
const EXPECTED_EXE_NAME_HASH: &str =
    "eeadbbc268b800cd5bc0e7d4951ce22ed362f5067232bd62cea75b593eb429c7";
const EXPECTED_DIR_HASH: &str = "ba8cd7f927010ad57aa9714b3193b9412fe69e863ba7fdc1cd896566bb67c0fc";
const EXPECTED_USERNAME_HASH: &str =
    "e27db24e649e24bae8e14f7f41329c50238f7bc43998cc2c86e8105c78115ba4";

const SALT: &str = "a63ac35cd956e95fa1b39ce195cedfd394119a5d950c803188e40fe9816b4f92";

fn main() {
    if cfg!(not(target_os = "windows")) {
        eprintln!("This binary is intended to run only on Windows.");
        exit(2);
    }

    let args: Vec<String> = env::args().collect();
    if args.len() > 1 && (args[1] == "--print-setup" || args[1] == "-setup") {
        if let Err(e) = print_setup() {
            eprintln!("Error while printing setup: {}", e);
            exit(1);
        }
        return;
    }

    // 1) MachineGuid
    let machine_guid = match read_machine_guid() {
        Ok(g) => g,
        Err(e) => {
            eprintln!("Не удалось прочитать MachineGuid: {}", e);
            exit(1);
        }
    };
    let machine_guid_norm = normalize(&machine_guid);
    if !EXPECTED_MACHINE_GUID_HASH.is_empty() {
        if !secure_eq_hex(
            &sha256_hex(&(SALT.to_owned() + &machine_guid_norm)),
            EXPECTED_MACHINE_GUID_HASH,
        ) {
            eprintln!("Machine GUID не совпадает. Прекращение работы.");
            exit(1);
        }
    }

    // 2) Executable path/name
    let exe_path = match env::current_exe() {
        Ok(p) => p,
        Err(e) => {
            eprintln!("Не удалось получить путь к исполняемому файлу: {}", e);
            exit(1);
        }
    };
    // canonicalize для нормализации (удаляет symlink'и)
    let exe_path = fs::canonicalize(&exe_path).unwrap_or(exe_path);
    let exe_name = exe_path
        .file_name()
        .map(|s| s.to_string_lossy().to_string())
        .unwrap_or_else(|| String::from(""));

    let exe_dir = exe_path
        .parent()
        .map(|p| p.to_string_lossy().to_string())
        .unwrap_or_else(|| String::from(""));

    let exe_name_norm = normalize(&exe_name);
    let exe_dir_norm = normalize(&exe_dir);

    if !EXPECTED_EXE_NAME_HASH.is_empty() {
        if !secure_eq_hex(
            &sha256_hex(&(SALT.to_owned() + &exe_name_norm)),
            EXPECTED_EXE_NAME_HASH,
        ) {
            eprintln!(
                "Имя исполняемого файла ({}) не совпадает. Прекращение работы.",
                exe_name
            );
            exit(1);
        }
    }
    if !EXPECTED_DIR_HASH.is_empty() {
        if !secure_eq_hex(
            &sha256_hex(&(SALT.to_owned() + &exe_dir_norm)),
            EXPECTED_DIR_HASH,
        ) {
            eprintln!("Папка ({}) не совпадает. Прекращение работы.", exe_dir);
            exit(1);
        }
    }

    // 3) Username
    let username = get_windows_username();
    let username_norm = normalize(&username);
    if !EXPECTED_USERNAME_HASH.is_empty() {
        if !secure_eq_hex(
            &sha256_hex(&(SALT.to_owned() + &username_norm)),
            EXPECTED_USERNAME_HASH,
        ) {
            eprintln!(
                "Имя пользователя ({}) не совпадает. Прекращение работы.",
                username
            );
            exit(1);
        }
    }

    println!("Все проверки пройдены. Программа выполняется.");
    // <-- ваша логика здесь -->
}

/// Нормализация:
/// - убираем ведущий префикс \\?\ и корректно обрабатываем \\?\UNC\... -> \\...
/// - trim + lowercase (Windows регистронезависим)
fn normalize(s: &str) -> String {
    let mut t = s.trim().to_string();

    // Убираем префикс "\\?\" (в строковом виде: "\\\\?\\")
    // и отдельно обрабатываем UNC: "\\?\UNC\server\share" -> "\\server\share"
    if t.starts_with(r"\\?\UNC\") {
        // "\\?\UNC\" имеет длину 8 символов (в строковом представлении)
        // преобразуем к "\\server\share" (удаляем "\\?\UNC" и добавляем ведущий "\\")
        // пример: "\\?\UNC\server\share" -> "\\server\share"
        let rest = &t[8..];
        t = format!(r"\\{}", rest);
    } else if t.starts_with(r"\\?\") {
        // просто удаляем первые 4 символа "\\?\"
        t = t[4..].to_string();
    }

    // Еще: иногда canonicalize возвращает пути с начальным '/' — но в Windows обычно нет.
    // Приводим к одному регистру
    t.to_lowercase()
}

/// Читает MachineGuid из реестра Windows
fn read_machine_guid() -> Result<String, String> {
    let hklm = RegKey::predef(HKEY_LOCAL_MACHINE);
    // Открываем ключ: SOFTWARE\Microsoft\Cryptography
    match hklm.open_subkey("SOFTWARE\\Microsoft\\Cryptography") {
        Ok(subk) => match subk.get_value::<String, &str>("MachineGuid") {
            Ok(val) => Ok(val),
            Err(e) => Err(format!("Ошибка чтения значения MachineGuid: {}", e)),
        },
        Err(e) => Err(format!("Ошибка открытия реестра: {}", e)),
    }
}

/// Получаем имя Windows-пользователя: сначала из env USERNAME, потом fallback на USER, иначе пусто
fn get_windows_username() -> String {
    if let Ok(u) = env::var("USERNAME") {
        if !u.is_empty() {
            return u;
        }
    }
    if let Ok(u) = env::var("USER") {
        if !u.is_empty() {
            return u;
        }
    }
    String::from("")
}

/// SHA256 -> hex
fn sha256_hex(s: &str) -> String {
    let mut hasher = Sha256::new();
    hasher.update(s.as_bytes());
    let result = hasher.finalize();
    hex::encode(result)
}

/// Сравнение hex-строк в константное время (по байтам)
fn secure_eq_hex(a_hex: &str, b_hex: &str) -> bool {
    let a = a_hex.as_bytes();
    let b = b_hex.as_bytes();
    if a.len() != b.len() {
        return false;
    }
    let mut diff: u8 = 0;
    for i in 0..a.len() {
        diff |= a[i] ^ b[i];
    }
    diff == 0
}

/// Печать данных для настройки — вызовите на целевой машине: myapp.exe --print-setup
fn print_setup() -> Result<(), String> {
    println!("=== Setup info (use these salted SHA256 values to fill constants) ===");
    println!("SALT: {:?}", SALT);
    // machine guid
    match read_machine_guid() {
        Ok(g) => {
            let n = normalize(&g);
            println!("MachineGuid: {}", g);
            println!("MachineGuid (normalized): {}", n);
            println!(
                "MachineGuid Salted SHA256: {}",
                sha256_hex(&(SALT.to_owned() + &n))
            );
            println!();
        }
        Err(e) => {
            println!("MachineGuid: error: {}", e);
            println!();
        }
    }

    // exe info
    match env::current_exe() {
        Ok(p) => {
            let p = fs::canonicalize(&p).unwrap_or(p);
            let name = p
                .file_name()
                .map(|s| s.to_string_lossy().to_string())
                .unwrap_or_else(|| String::from(""));
            let dir = p
                .parent()
                .map(|p| p.to_string_lossy().to_string())
                .unwrap_or_else(|| String::from(""));

            let name_n = normalize(&name);
            let dir_n = normalize(&dir);

            println!("Executable path: {}", p.to_string_lossy());
            println!("Executable name: {}", name);
            println!("Executable name (normalized): {}", name_n);
            println!(
                "Executable name Salted SHA256: {}",
                sha256_hex(&(SALT.to_owned() + &name_n))
            );
            println!();
            println!("Executable dir: {}", dir);
            println!("Executable dir (normalized): {}", dir_n);
            println!(
                "Executable dir Salted SHA256: {}",
                sha256_hex(&(SALT.to_owned() + &dir_n))
            );
            println!();
        }
        Err(e) => {
            println!("Executable path: error: {}", e);
            println!();
        }
    }

    // username
    let username = get_windows_username();
    let username_n = normalize(&username);
    println!("Windows username: {}", username);
    println!("Windows username (normalized): {}", username_n);
    println!(
        "Windows username Salted SHA256: {}",
        sha256_hex(&(SALT.to_owned() + &username_n))
    );

    println!();
    println!("Скопируйте соответствующие 'Salted SHA256' значения в константы в исходнике и пересоберите программу.");
    Ok(())
}
