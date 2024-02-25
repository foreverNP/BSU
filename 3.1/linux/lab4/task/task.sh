#!/bin/bash

# Функция для отображения ID процесса и его родителя
print_process_info() {
    echo "Процесс с PID $BASHPID, PPID $PPID"
}

# Основной код для построения дерева
create_process_tree() {
    print_process_info  # Отображаем информацию о процессе

    # Создаём процесс 1
    if [ "$1" == "1" ]; then
        echo "Процесс с PID $BASHPID порождает процессы 2, 3 и 4"
        
        (create_process_tree 2) &
        (create_process_tree 3) &
        (create_process_tree 4) &
        wait

    # Процесс 2 без потомков
    elif [ "$1" == "2" ]; then
        print_process_info

    # Процесс 3 должен вызвать exec с командой time
    elif [ "$1" == "3" ]; then
        print_process_info
        echo "Процесс с PID $BASHPID заменяется на выполнение команды 'time'"
        exec time sleep 1

    # Процесс 4 порождает процесс 5
    elif [ "$1" == "4" ]; then
        print_process_info
        echo "Процесс с PID $BASHPID порождает процесс 5"
        
        (create_process_tree 5) &
        wait

    # Процесс 5 порождает процессы 6 и 7
    elif [ "$1" == "5" ]; then
        print_process_info
        echo "Процесс с PID $BASHPID порождает процессы 6 и 7"

        (create_process_tree 6) &
        (create_process_tree 7) &
        wait

    # Процесс 6 и 7 завершаются
    elif [ "$1" == "6" ] || [ "$1" == "7" ]; then
        print_process_info
    fi

    # Сообщаем о завершении работы процесса
    echo "Процесс с PID $BASHPID, PPID $PPID завершает работу"
}

# Начальный процесс (внешний)
echo "Процесс с PID $BASHPID порождает процесс 1"
(create_process_tree 1) &
wait
