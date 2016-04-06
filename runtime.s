########################################
############### RUN-TIME ###############
########################################

#malloc
L1:
	# a0 -- size in bytes (already x4)
	li $v0, 9
	syscall
	jr $ra

exit:
	li $v0, 10
	syscall
	jr $ra

## Daniel J. Ellard -- 03/13/94
## printf.asm--
## an implementation of a simple printf work-alike.

## printf--
## A simple printf-like function. Understands just the basic forms
## of the %s, %d, %c, and %% formats, and can only have 3 embedded
## formats (so that all of the parameters are passed in registers).
## If there are more than 3 embedded formats, all but the first 3 are
## completely ignored (not even printed).
## Register Usage:
## $a0,$s0 - pointer to format string
## $a1,$s1 - format argument 1 (optional)
## $a2,$s2 - format argument 2 (optional)
## $a3,$s3 - format argument 3 (optional)
## $s4 - count of formats processed.
## $s5 - char at $s4.
## $s6 - pointer to printf buffer
##
L0:
	subu $sp, $sp, 56 # set up the stack frame,
	sw $ra, 32($sp) # saving the local environment.
	sw $fp, 28($sp)
	sw $s0, 24($sp)
	sw $s1, 20($sp)
	sw $s2, 16($sp)
	sw $s3, 12($sp)
	sw $s4, 8($sp)
	sw $s5, 4($sp)
	sw $s6, 0($sp)
	sw $s7, 36($sp)
	sw $t0, 40($sp)
	sw $t1, 44($sp)
	sw $t2, 48($sp)
	sw $t3, 52($sp)
	addu $fp, $sp, 52

# grab the arguments:
	move $s0, $a0 # fmt string
	move $s1, $a1 # arg1 (optional)
	move $s2, $a2 # arg2 (optional)
	move $s3, $a3 # arg3 (optional)
	lw $s7, 16($v1)# arg4 (optional)
	lw $t0, 20($v1)# arg5 (optional)

	li $s4, 0 # set # of formats = 0
	la $s6, printf_buf # set s6 = base of printf buffer.

printf_loop: # process each character in the fmt:
	lb $s5, 0($s0) # get the next character, and then
	addu $s0, $s0, 1 # bump up $s0 to the next character.

	beq $s5, '%', printf_fmt # if the fmt character, then do fmt.
	beq $0, $s5, printf_end # if zero, then go to end.

printf_putc:
	sb $s5, 0($s6) # otherwise, just put this char
	sb $0, 1($s6) # into the printf buffer,
	move $a0, $s6 # and then print it with the
	li $v0, 4 # print_str syscall
	syscall

	b printf_loop # loop on.

printf_fmt:
	lb $s5, 0($s0) # see what the fmt character is,
	addu $s0, $s0, 1 # and bump up the pointer.

	beq $s4, 5, printf_loop # if we've already processed 3 args,
# then *ignore* this fmt.
	beq $s5, '0', printf_pre
	beq $s5, 'd', printf_int # if 'd', print as a decimal integer.
	beq $s5, 's', printf_str # if 's', print as a string.
	beq $s5, 'c', printf_char # if 'c', print as a ASCII char.
	beq $s5, '%', printf_perc # if '%', print a '%'
	b printf_loop # otherwise, just continue.

printf_shift_args: # shift over the fmt args,
	move $s1, $s2 # $s1 = $s2
	move $s2, $s3 # $s2 = $s3
	move $s3, $s7 # $s3 = $s7
	move $s7, $t0

	add $s4, $s4, 1 # increment # of args processed.

	b printf_loop # and continue the main loop.
	
printf_pre:
	lb $s5, ($s0)
	sub $s5, $s5, 48
	add $s0, $s0, 2
	
	li $t1, 0
	move $t2, $s1
printf_digits_begin:
	beqz $t2, printf_digits_end
	add $t1, $t1, 1
	div $t2, $t2, 10
	b printf_digits_begin
	
printf_digits_end:
	sub $t1, $s5, $t1
	
printf_pre_output_zero_begin:
	beqz $t1, printf_pre_output_zero_end
	sub $t1, $t1, 1
	li $a0, 0
	li $v0, 1
	syscall
	b printf_pre_output_zero_begin
	
printf_pre_output_zero_end:
	move $a0, $s1
	li $v0, 1
	syscall
	b printf_shift_args
	

printf_int: # deal with a %d:
	move $a0, $s1 # do a print_int syscall of $s1.
	li $v0, 1
	syscall
	b printf_shift_args # branch to printf_shift_args

printf_str: # deal with a %s:
	move $a0, $s1 # do a print_string syscall of $s1.
	li $v0, 4
	syscall
	b printf_shift_args # branch to printf_shift_args

printf_char: # deal with a %c:
	sb $s1, 0($s6) # fill the buffer in with byte $s1,
	sb $0, 1($s6) # and then a null.
	move $a0, $s6 # and then do a print_str syscall
	li $v0, 4 # on the buffer.
	syscall
	b printf_shift_args # branch to printf_shift_args

printf_perc: # deal with a %%:
	li $s5, '%' # (this is redundant)
	sb $s5, 0($s6) # fill the buffer in with byte %,
	sb $0, 1($s6) # and then a null.
	move $a0, $s6 # and then do a print_str syscall
	li $v0, 4 # on the buffer.
	syscall
	b printf_loop # branch to printf_loop

printf_end:
	lw $t1, 44($sp)
	lw $t2, 48($sp)
	lw $t3, 52($sp)
	lw $s7, 36($sp)
	lw $t0, 40($sp)
	lw $ra, 32($sp) # restore the prior environment:
	lw $fp, 28($sp)
	lw $s0, 24($sp)
	lw $s1, 20($sp)
	lw $s2, 16($sp)
	lw $s3, 12($sp)
	lw $s4, 8($sp)
	lw $s5, 4($sp)
	lw $s6, 0($sp)
	addu $sp, $sp, 56 # release the stack frame.
	jr $ra # return.

.data
	printf_buf: .space 2

