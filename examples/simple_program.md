# 8086 CPU 示例程序

## 简单的汇编程序示例

```assembly
; 简单的 8086 程序示例
; 这个程序演示了基本的指令执行

START:
    MOV AX, 0x1234    ; 将立即数 0x1234 加载到 AX 寄存器
    MOV BX, 0x5678    ; 将立即数 0x5678 加载到 BX 寄存器
    ADD AX, BX        ; AX = AX + BX
    HLT               ; 停止 CPU
```

## 机器码表示

| 地址 | 机器码 | 指令 |
|------|--------|------|
| 0x0000 | B8 34 12 | MOV AX, 0x1234 |
| 0x0002 | BB 78 56 | MOV BX, 0x5678 |
| 0x0004 | 01 D8 | ADD AX, BX |
| 0x0006 | F4 | HLT |

## 执行流程

1. **初始状态**
   - IP = 0x0000
   - AX = 0x0000
   - BX = 0x0000

2. **执行 MOV AX, 0x1234**
   - 从内存地址 0x0000 取指令
   - AX = 0x1234
   - IP = 0x0002

3. **执行 MOV BX, 0x5678**
   - 从内存地址 0x0002 取指令
   - BX = 0x5678
   - IP = 0x0004

4. **执行 ADD AX, BX**
   - 从内存地址 0x0004 取指令
   - AX = 0x1234 + 0x5678 = 0x68AC
   - IP = 0x0006

5. **执行 HLT**
   - CPU 停止
   - 最终 AX = 0x68AC

## 寄存器说明

### 通用寄存器
- **AX** (Accumulator): 累加器，用于算术运算
- **BX** (Base): 基址寄存器，用于寻址
- **CX** (Count): 计数器，用于循环
- **DX** (Data): 数据寄存器

### 段寄存器
- **CS** (Code Segment): 代码段
- **DS** (Data Segment): 数据段
- **SS** (Stack Segment): 栈段
- **ES** (Extra Segment): 附加段

### 指针和索引寄存器
- **IP** (Instruction Pointer): 指令指针
- **SP** (Stack Pointer): 栈指针
- **BP** (Base Pointer): 基址指针
- **SI** (Source Index): 源变址
- **DI** (Destination Index): 目的变址
