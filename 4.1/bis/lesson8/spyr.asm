.MODEL small
.STACK 100h
.DATA

FName       DB  'myfile.bin',0
Handle      DW  ?
Buffer      DB  ?
Lang        DB  0
ShiftState  DB  0
AltState    DB  0
CapsState   DB  0

ScanToEN    DB  0,27,'1234567890-=',8,9
            DB  'qwertyuiop[]',13,0
            DB  'asdfghjkl;',39,'`',0,'\'
            DB  'zxcvbnm,./',0,'*',0,' '
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0

ScanToENShift DB 0,27,'!@#$%^&*()_+',8,9
            DB  'QWERTYUIOP{}',13,0
            DB  'ASDFGHJKL:"~',0,'|'
            DB  'ZXCVBNM<>?',0,'*',0,' '
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0

ScanToRU    DB  0,27,'1234567890-=',8,9
            DB  0E9h,0F6h,0F3h,0EAh,0E5h,0EDh,0E3h,0F8h,0F9h,0E7h,0F5h,0FAh,13,0
            DB  0F4h,0FBh,0E2h,0E0h,0EFh,0F0h,0EEh,0EBh,0E4h,0E6h,0FDh,39,0F1h,0,'\'
            DB  0FFh,0F7h,0F1h,0ECh,0E8h,0F2h,0FCh,0E1h,0FEh,'.',0,'*',0,' '
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0

ScanToRUShift DB 0,27,'!"',0FCh,';%:?*()_+',8,9
            DB  0C9h,0D6h,0D3h,0CAh,0C5h,0CDh,0C3h,0D8h,0D9h,0C7h,0D5h,0DAh,13,0
            DB  0D4h,0DBh,0C2h,0C0h,0CFh,0D0h,0CEh,0CBh,0C4h,0C6h,0DDh,39,0D1h,0,'/'
            DB  0DFh,0D7h,0D1h,0CCh,0C8h,0D2h,0DCh,0C1h,0DEh,',',0,'*',0,' '
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0
            DB  0,0,0,0,0,0,0,0,0,0

.CODE
Start:
    mov     ax, @data
    mov     ds, ax
    
    mov     ah, 3Dh
    mov     al, 0
    mov     dx, OFFSET FName
    int     21h
    jnc     FileOk
    jmp     Exit
FileOk:
    mov     Handle, ax
    
ReadLoop:
    mov     ah, 3Fh
    mov     bx, Handle
    mov     cx, 1
    mov     dx, OFFSET Buffer
    int     21h
    jnc     CheckEOF
    jmp     CloseFile
CheckEOF:
    cmp     ax, 0
    jne     ProcessByte
    jmp     CloseFile
    
ProcessByte:
    mov     al, Buffer
    
    cmp     al, 2Ah
    jne     ChkRShift
    mov     ShiftState, 1
    jmp     CheckAltShift
ChkRShift:
    cmp     al, 36h
    jne     ChkLShiftUp
    mov     ShiftState, 1
    jmp     CheckAltShift
ChkLShiftUp:
    cmp     al, 0AAh
    jne     ChkRShiftUp
    mov     ShiftState, 0
    jmp     ReadLoop
ChkRShiftUp:
    cmp     al, 0B6h
    jne     ChkAlt
    mov     ShiftState, 0
    jmp     ReadLoop
    
ChkAlt:
    cmp     al, 38h
    jne     ChkAltUp
    mov     AltState, 1
    jmp     ReadLoop
ChkAltUp:
    cmp     al, 0B8h
    jne     ChkCaps
    mov     AltState, 0
    jmp     ReadLoop
    
ChkCaps:
    cmp     al, 3Ah
    jne     ChkCapsUp
    xor     CapsState, 1
    jmp     ReadLoop
ChkCapsUp:
    cmp     al, 0BAh
    jne     ChkRelease
    jmp     ReadLoop
    
ChkRelease:
    cmp     al, 80h
    jb      NotRelease
    jmp     ReadLoop
NotRelease:

CheckAltShift:
    cmp     AltState, 1
    jne     PrintKey
    ; Alt is pressed, check if this is a shift key
    cmp     al, 2Ah           ; Left Shift
    jne     ChkRuSwitch
    mov     Lang, 0           ; Alt+Left Shift = English
    jmp     ReadLoop
ChkRuSwitch:
    cmp     al, 36h           ; Right Shift  
    jne     PrintKey          ; Not a shift key, so print normally
    mov     Lang, 1           ; Alt+Right Shift = Russian
    jmp     ReadLoop
ContinueRead:
    jmp     ReadLoop
    
    
PrintKey:
    xor     ah, ah
    mov     bx, ax
    
    cmp     Lang, 0
    jne     DoRussian
    
    cmp     ShiftState, 1
    jne     EnNoShift
    mov     al, ScanToENShift[bx]
    jmp     CheckCapsEn
EnNoShift:
    mov     al, ScanToEN[bx]
CheckCapsEn:
    cmp     CapsState, 1
    jne     Output
    cmp     al, 'a'
    jb      ChkUpperEn
    cmp     al, 'z'
    ja      Output
    sub     al, 32
    jmp     Output
ChkUpperEn:
    cmp     al, 'A'
    jb      Output
    cmp     al, 'Z'
    ja      Output
    add     al, 32
    jmp     Output
    
DoRussian:
    cmp     ShiftState, 1
    jne     RuNoShift
    mov     al, ScanToRUShift[bx]
    jmp     CheckCapsRu
RuNoShift:
    mov     al, ScanToRU[bx]
CheckCapsRu:
    cmp     CapsState, 1
    jne     Output
    cmp     al, 0E0h
    jb      Output
    cmp     al, 0FFh
    ja      Output
    cmp     al, 0EFh
    jbe     RuLower
    cmp     al, 0C0h
    jb      Output
    cmp     al, 0DFh
    ja      Output
    add     al, 32
    jmp     Output
RuLower:
    sub     al, 32
    
Output:
    cmp     al, 0
    je      ContinueRead2
    mov     ah, 02h
    mov     dl, al
    int     21h
ContinueRead2:
    jmp     ReadLoop
    
CloseFile:
    mov     ah, 3Eh
    mov     bx, Handle
    int     21h
    
Exit:
    mov     ax, 4C00h
    int     21h
    
END Start