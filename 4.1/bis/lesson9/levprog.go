//go:build windows
// +build windows

package main

import (
	"crypto/sha256"
	"crypto/subtle"
	"encoding/hex"
	"errors"
	"fmt"
	"log"
	"os"
	"os/exec"
	"os/user"
	"path/filepath"
	"runtime"
	"strings"
)

const (
	expectedMachineIDHash = "a0b9577f2809fdc013592d41eb469319b0bf27712db8c615069013862645f4d9"
	expectedExeNameHash   = "eeadbbc268b800cd5bc0e7d4951ce22ed362f5067232bd62cea75b593eb429c7"
	expectedDirHash       = "ba8cd7f927010ad57aa9714b3193b9412fe69e863ba7fdc1cd896566bb67c0fc"
	expectedUserHash      = "e27db24e649e24bae8e14f7f41329c50238f7bc43998cc2c86e8105c78115ba4"
)

const SALT = "a63ac35cd956e95fa1b39ce195cedfd394119a5d950c803188e40fe9816b4f92"

func main() {
	if runtime.GOOS != "windows" {
		log.Fatalf("This binary is built to run only on Windows. runtime.GOOS=%s", runtime.GOOS)
	}

	if len(os.Args) > 1 && (os.Args[1] == "--print-setup" || os.Args[1] == "-setup") {
		printSetup()
		return
	}

	// 1) Machine ID (MachineGuid из реестра)
	mid, err := getMachineIDWindows()
	if err != nil {
		log.Fatalf("Не удалось получить MachineGuid: %v", err)
	}
	midNorm := normalize(mid)
	if expectedMachineIDHash != "" {
		if !secureCompareHash(SALT+midNorm, expectedMachineIDHash) {
			log.Fatalf("Machine GUID не совпадает. Прекращение работы.")
		}
	}

	// 2) Путь к исполняемому файлу и имя
	exePath, err := os.Executable()
	if err != nil {
		log.Fatalf("Не удалось получить путь к исполняемому файлу: %v", err)
	}
	exePath, _ = filepath.EvalSymlinks(exePath)
	exePath, _ = filepath.Abs(exePath)
	exeName := filepath.Base(exePath)
	exeDir := filepath.Dir(exePath)

	// нормализуем (clean + lower) т.к. Windows регистронезависим
	exeNameNorm := normalize(exeName)
	exeDirNorm := normalize(exeDir)

	if expectedExeNameHash != "" {
		if !secureCompareHash(SALT+exeNameNorm, expectedExeNameHash) {
			log.Fatalf("Имя выполняемого файла (%s) не совпадает с ожидаемым. Прекращение работы.", exeName)
		}
	}
	if expectedDirHash != "" {
		if !secureCompareHash(SALT+exeDirNorm, expectedDirHash) {
			log.Fatalf("Папка (%s) не совпадает с ожидаемой. Прекращение работы.", exeDir)
		}
	}

	// 3) Текущий пользователь (USERNAME)
	username := getWindowsUsername()
	usernameNorm := normalize(username)
	if expectedUserHash != "" {
		if !secureCompareHash(SALT+usernameNorm, expectedUserHash) {
			log.Fatalf("Имя пользователя (%s) не совпадает с ожидаемым. Прекращение работы.", username)
		}
	}

	fmt.Println("Все проверки пройдены. Программа выполняется.")
}

// ------------------------------------------------------------
// Helpers
// ------------------------------------------------------------

func normalize(s string) string {
	return strings.ToLower(strings.TrimSpace(filepath.Clean(s)))
}

func secureCompareHash(valueWithSalt string, expectedHex string) bool {
	sum := sha256.Sum256([]byte(valueWithSalt))
	sumHex := hex.EncodeToString(sum[:])
	sumHex = strings.ToLower(sumHex)
	expectedHex = strings.ToLower(strings.TrimSpace(expectedHex))
	if len(sumHex) != len(expectedHex) {
		return false
	}
	return subtle.ConstantTimeCompare([]byte(sumHex), []byte(expectedHex)) == 1
}

// ------------------------------------------------------------
// Setup printing (для генерации хэшей на целевой машине)
// ------------------------------------------------------------
func printSetup() {
	fmt.Println("=== Setup info (use these salted SHA256 values to fill constants) ===")
	fmt.Printf("SALT: %q\n\n", SALT)

	// machine id
	mid, err := getMachineIDWindows()
	if err != nil {
		fmt.Printf("MachineGuid: error: %v\n", err)
	} else {
		n := normalize(mid)
		fmt.Printf("MachineGuid: %s\nMachineGuid (normalized): %s\nMachineGuid Salted SHA256: %s\n\n", mid, n, sha256Hex(SALT+n))
	}

	// exe path / name
	exePath, err := os.Executable()
	if err != nil {
		fmt.Printf("Executable path: error: %v\n", err)
	} else {
		exePath, _ = filepath.EvalSymlinks(exePath)
		exePath, _ = filepath.Abs(exePath)
		exeName := filepath.Base(exePath)
		exeDir := filepath.Dir(exePath)
		fmt.Printf("Executable path: %s\n", exePath)
		fmt.Printf("Executable name: %s\nExecutable name (normalized): %s\nExecutable name Salted SHA256: %s\n\n",
			exeName, normalize(exeName), sha256Hex(SALT+normalize(exeName)))
		fmt.Printf("Executable dir: %s\nExecutable dir (normalized): %s\nExecutable dir Salted SHA256: %s\n\n",
			exeDir, normalize(exeDir), sha256Hex(SALT+normalize(exeDir)))
	}

	// username
	username := getWindowsUsername()
	fmt.Printf("Windows username: %s\nWindows username (normalized): %s\nWindows username Salted SHA256: %s\n\n",
		username, normalize(username), sha256Hex(SALT+normalize(username)))

	fmt.Println("Скопируйте соответствующие 'Salted SHA256' в константы и пересоберите программу.")
}

func sha256Hex(s string) string {
	sum := sha256.Sum256([]byte(s))
	return hex.EncodeToString(sum[:])
}

// ------------------------------------------------------------
// Получение Windows MachineGuid из реестра
// ------------------------------------------------------------
func getMachineIDWindows() (string, error) {
	// reg query HKLM\SOFTWARE\Microsoft\Cryptography /v MachineGuid
	cmd := exec.Command("reg", "query", `HKLM\SOFTWARE\Microsoft\Cryptography`, "/v", "MachineGuid")
	outb, err := cmd.Output()
	if err != nil {
		// иногда reg может вернуть ошибку, но вывести что-то — попытаемся прочитать вывод в stderr
		return "", err
	}
	out := string(outb)
	for _, line := range strings.Split(out, "\n") {
		line = strings.TrimSpace(line)
		// пример строки: MachineGuid    REG_SZ    4c4b...-...
		if strings.HasPrefix(line, "MachineGuid") || strings.Contains(line, "MachineGuid") {
			fields := strings.Fields(line)
			if len(fields) >= 3 {
				return fields[len(fields)-1], nil
			}
		}
	}
	// fallback: попытка найти GUID в любом месте вывода
	parts := strings.Split(out, "MachineGuid")
	if len(parts) > 1 {
		rest := parts[1]
		fields := strings.Fields(rest)
		if len(fields) > 0 {
			return fields[len(fields)-1], nil
		}
	}
	return "", errors.New("MachineGuid not found in reg output")
}

// ------------------------------------------------------------
// Получение текущего пользователя Windows
// ------------------------------------------------------------
func getWindowsUsername() string {
	// сначала env variable (наиболее надёжно в большинстве сценариев)
	u := os.Getenv("USERNAME")
	if u != "" {
		return u
	}

	usr, err := user.Current()
	if err == nil && usr.Username != "" {
		// на Windows usr.Username может содержать DOMAIN\user — оставим только после слэша
		if strings.Contains(usr.Username, `\`) {
			parts := strings.Split(usr.Username, `\`)
			return parts[len(parts)-1]
		}
		return usr.Username
	}

	if u = os.Getenv("USER"); u != "" {
		return u
	}
	return ""
}
