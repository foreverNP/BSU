semaphore mutex = 1;       /* Общие переменные: семафоры */
int active = 0;            /* Счетчики и информация */
queue waitingQueue;        /* Очередь для ожидающих процессов */
boolean must_wait = false; /* о состоянии */

void use_resource()
{
    /* Вход в критическую секцию */
    semWait(mutex);
    if (must_wait || waitingQueue.size() > 0)
    {                      /* Если есть (или было) 3 */
        semSignal(mutex);  /* Покинуть взаимное исключение */
        waitingQueue.Wait; /* Добавляет процесс в очередь и блокирует его */
        semWait(mutex);    /* Для обновления счетчика количества активных */
    }
    ++active;                /* Обновление количества активных */
    must_wait = active == 3; /* и запоминание достижения значения 3 */
    semSignal(mutex);        /* Покидаем взаимное исключение */

    /* Критический участок */
    /* Действия с ресурсом */

    semWait(mutex); /* Вход во взаимное исключение */
    --active;       /* Обновление счетчика active */
    if (active == 0)
    { /* Последний процесс? */
        int n;
        if (waitingQueue.size() < 3)
            n = waitingQueue.size();
        else
            n = 3; /* Если да - разблокируем до 3 ждущих процессов */
        while (n > 0)
        {
            waitingQueue.Signal; /* Разблокировать процесс */
            --n;
        }
        must_wait = false; /* Покинули все активные процессы */
    }
    semSignal(mutex); /* Покинули взаимное исключение */
}
