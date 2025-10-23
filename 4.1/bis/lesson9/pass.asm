
.model tiny

.data
input    db 5,?,5 dup (?)
errmsg   db 'FAIL',10,13,'$'
scmsg    db 'SUCCESS',10,13,'$'

.code
.startup
main proc
    ; password
    mov     bx,15Fh
    call    todigit
    push    ax
    mov     bx,120h
    call    todigit
    push    ax
    mov     bx,15Ah
    call    todigit
    push    ax
    mov     bx,166h
    call    todigit
    push    ax

    ; input
    mov 	ah,10
	lea		dx,input
	int 	21h

    mov     ah,4
    cmp     ah,input+1
    jne     miscompare     

    ; comparison
    mov     bx,2
compsym:
    pop     ax
    mov     al,input[bx]
    cmp     ah,al
    jne     miscompare
    inc     bx
    cmp     bl,input+1
    jne     compsym
    jmp     success
miscompare:
    lea     dx,errmsg
    jmp     outmsg
success:
    lea     dx,scmsg
outmsg:
    mov     ah,9h
    int     21h

;  … 

      mov 	ah,4ch
	int 	21h
    ret
main endp

todigit proc
; BX byte offset
; returns symbol in AH
    mov     al,byte ptr [bx]
    mov     ah,0
    mov     dl,0Ah
    div     dl
    xor     ah,30h
    ret
todigit endp
end

