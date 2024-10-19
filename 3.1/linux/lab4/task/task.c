#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

void create_process_tree();

int main() {
    // Получаем и выводим PID и PPID процесса при запуске программы
    pid_t pid = getpid();
    pid_t ppid = getppid();
    
    printf("Процесс с PID %d и PPID %d запущен.\n", pid, ppid);
    
    // Создание генеалогического дерева процессов
    create_process_tree();

    return 0;
}

void create_process_tree() {
    pid_t pids[7]; // Массив для хранения PID порожденных процессов
    
    // Процесс 1 порождает процессы 2, 3, 4
    pids[0] = getpid();  // Процесс 1
    printf("Процесс с PID %d и PPID %d порождает процесс 2.\n", getpid(), getppid());
    
    if ((pids[1] = fork()) == 0) { // Процесс 2
        printf("Процесс с PID %d и PPID %d запущен.\n", getpid(), getppid());
        printf("Процесс с PID %d и PPID %d завершает работу.\n", getpid(), getppid());
        exit(0); // Завершаем процесс 2
    }
    
    wait(NULL); // Ожидаем завершения процесса 2

    printf("Процесс с PID %d и PPID %d порождает процесс 3.\n", getpid(), getppid());
    
    if ((pids[2] = fork()) == 0) { // Процесс 3
        printf("Процесс с PID %d и PPID %d запущен.\n", getpid(), getppid());
        
        // Процесс 3 вызывает exec для команды time
        printf("Процесс с PID %d вызывает exec команды time.\n", getpid());
        execlp("time", "time", "sleep", "2", NULL);
        
        // Если exec не удался, выводим сообщение об ошибке и завершаем процесс
        perror("exec failed");
        exit(1);
    }
    
    wait(NULL); // Ожидаем завершения процесса 3

    printf("Процесс с PID %d и PPID %d порождает процесс 4.\n", getpid(), getppid());
    
    if ((pids[3] = fork()) == 0) { // Процесс 4
        printf("Процесс с PID %d и PPID %d запущен.\n", getpid(), getppid());
        printf("Процесс с PID %d и PPID %d завершает работу.\n", getpid(), getppid());
        exit(0); // Завершаем процесс 4
    }
    
    wait(NULL); // Ожидаем завершения процесса 4

    // Процесс 3 порождает процесс 5
    if (pids[2] > 0) {
        printf("Процесс с PID %d порождает процесс 5.\n", pids[2]);
        
        if ((pids[4] = fork()) == 0) { // Процесс 5
            printf("Процесс с PID %d и PPID %d запущен.\n", getpid(), getppid());
            printf("Процесс с PID %d и PPID %d завершает работу.\n", getpid(), getppid());
            exit(0); // Завершаем процесс 5
        }
        
        wait(NULL); // Ожидаем завершения процесса 5
    }
    
    // Процесс 5 порождает процессы 6 и 7
    if (pids[4] > 0) {
        printf("Процесс с PID %d порождает процесс 6.\n", pids[4]);
        
        if ((pids[5] = fork()) == 0) { // Процесс 6
            printf("Процесс с PID %d и PPID %d запущен.\n", getpid(), getppid());
            printf("Процесс с PID %d и PPID %d завершает работу.\n", getpid(), getppid());
            exit(0); // Завершаем процесс 6
        }
        
        wait(NULL); // Ожидаем завершения процесса 6
        
        printf("Процесс с PID %d порождает процесс 7.\n", pids[4]);
        
        if ((pids[6] = fork()) == 0) { // Процесс 7
            printf("Процесс с PID %d и PPID %d запущен.\n", getpid(), getppid());
            printf("Процесс с PID %d и PPID %d завершает работу.\n", getpid(), getppid());
            exit(0); // Завершаем процесс 7
        }
        
        wait(NULL); // Ожидаем завершения процесса 7
    }
    
    // Завершаем родительский процесс
    printf("Процесс с PID %d и PPID %d завершает работу.\n", getpid(), getppid());
}
