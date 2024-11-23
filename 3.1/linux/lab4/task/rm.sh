#!/bin/bash

# вывод информации о процессе
print_process_info() {
    local pid=$1
    local ppid=$2
    local relative_id=$3
    echo "Процесс с номером $relative_id, PID = $pid и PPID = $ppid"
}

main() {
    local parent_pid=$1
    local child_id=$2

    # Получаем текущий PID
    current_pid=$$

    print_process_info $current_pid $parent_pid $child_id
    echo "Процесс с PID $parent_pid породил процесс с PID $current_pid"
    
    # Процесс с ID 3 запускает команду `time` с измерением времени выполнения `ls`
    if [ $child_id -eq 3 ]; then
        echo "Запуск команды 'time ls' в процессе с PID $current_pid"
        exec time ls
    fi

    case $child_id in
        1)
            bash $0 $current_pid 2 &
            bash $0 $current_pid 3 &
            bash $0 $current_pid 4 &
            ;;
        2)
            bash $0 $current_pid 5 &
            ;;
        5)
            bash $0 $current_pid 6 &
            bash $0 $current_pid 7 &
            ;;
    esac

    # Выводим информацию о завершении процесса
    echo "Процесс с номером $child_id, PID = $current_pid и PPID = $parent_pid завершает работу"
    
    wait
}

if [ $# -eq 0 ]; then
    # Основной процесс
    main_pid=$$
    print_process_info $main_pid $PPID 1
    echo "Процесс с относительным ID 1 порождает процессы 2, 3, 4"

    # Создаем процессы 2, 3, и 4
    bash $0 $main_pid 2 &
    bash $0 $main_pid 3 &
    bash $0 $main_pid 4 &

    # Ждем завершения дочерних процессов
    wait

    echo "Процесс с номером 1, PID = $main_pid и PPID = $PPID завершает работу"
else
    main $1 $2
fi
