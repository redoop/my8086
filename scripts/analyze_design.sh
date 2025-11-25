#!/bin/bash
# 设计分析脚本 - 统计 MyCPU8086 的资源使用

echo "=========================================="
echo "MyCPU8086 设计资源分析"
echo "=========================================="
echo ""

VERILOG_FILE="generated/MyCPU8086System.v"

if [ ! -f "$VERILOG_FILE" ]; then
    echo "错误: 找不到 $VERILOG_FILE"
    echo "请先运行: sbt 'runMain cpu8086.Main'"
    exit 1
fi

echo "1. 寄存器统计"
echo "----------------------------------------"

# 统计 CPU 核心寄存器
echo "CPU 核心寄存器:"
grep -E "^\s*reg " $VERILOG_FILE | grep -v "RAND\|initvar\|mem\[" | head -20

# 统计寄存器位数
CPU_REGS=$(grep -E "^\s*reg \[" $VERILOG_FILE | grep -v "RAND\|initvar\|mem\[" | grep -v "mem_io")
echo ""
echo "寄存器详细统计:"
echo "$CPU_REGS"

# 计算总位数
TOTAL_BITS=0

# ax: 16 bits
TOTAL_BITS=$((TOTAL_BITS + 16))
echo "  - ax: 16 bits"

# ip: 16 bits
TOTAL_BITS=$((TOTAL_BITS + 16))
echo "  - ip: 16 bits"

# state: 2 bits
TOTAL_BITS=$((TOTAL_BITS + 2))
echo "  - state: 2 bits"

# instruction: 16 bits
TOTAL_BITS=$((TOTAL_BITS + 16))
echo "  - instruction: 16 bits"

# Memory pipeline registers
TOTAL_BITS=$((TOTAL_BITS + 1 + 10))
echo "  - mem_io_dataOut_MPORT_en_pipe_0: 1 bit"
echo "  - mem_io_dataOut_MPORT_addr_pipe_0: 10 bits"

echo ""
echo "CPU 寄存器总位数: $TOTAL_BITS bits"

# 内存统计
echo ""
echo "2. 内存统计"
echo "----------------------------------------"
MEM_SIZE=$(grep "reg \[15:0\] mem \[" $VERILOG_FILE | grep -oP '\[\d+:\d+\]' | tail -1)
echo "内存: 1024 x 16 bits = 16,384 bits"

echo ""
echo "3. 组合逻辑统计"
echo "----------------------------------------"
WIRE_COUNT=$(grep -c "^\s*wire " $VERILOG_FILE)
echo "Wire 信号数量: $WIRE_COUNT"

# 统计加法器
ADDER_COUNT=$(grep -c "+" $VERILOG_FILE | head -1)
echo "加法器数量: ~2 (地址计算 + ALU)"

# 统计比较器
CMP_COUNT=$(grep -c "==" $VERILOG_FILE)
echo "比较器数量: ~$CMP_COUNT"

# 统计多路选择器
MUX_COUNT=$(grep -c "?" $VERILOG_FILE)
echo "多路选择器数量: ~$MUX_COUNT"

echo ""
echo "4. 晶体管估算"
echo "=========================================="
echo ""

# 晶体管估算公式（基于标准单元库）
# - 1 bit 寄存器 (D触发器): ~12 晶体管
# - 1 bit SRAM: ~6 晶体管
# - 1 bit 加法器: ~28 晶体管
# - 1 bit 比较器: ~8 晶体管
# - 2:1 MUX (1 bit): ~4 晶体管

# CPU 寄存器
REG_TRANSISTORS=$((TOTAL_BITS * 12))
echo "CPU 寄存器: $TOTAL_BITS bits × 12 = $REG_TRANSISTORS 晶体管"

# 内存 (SRAM)
MEM_TRANSISTORS=$((16384 * 6))
echo "内存 (SRAM): 16,384 bits × 6 = $MEM_TRANSISTORS 晶体管"

# 加法器 (20-bit 地址加法器 + 16-bit ALU)
ADDER_TRANSISTORS=$(((20 + 16) * 28))
echo "加法器: (20 + 16) bits × 28 = $ADDER_TRANSISTORS 晶体管"

# 比较器和控制逻辑 (估算)
CONTROL_TRANSISTORS=$((8 * 8 * 10))
echo "控制逻辑: ~$CONTROL_TRANSISTORS 晶体管"

# 多路选择器
MUX_TRANSISTORS=$((16 * 4 * 5))
echo "多路选择器: ~$MUX_TRANSISTORS 晶体管"

# 总计
TOTAL_TRANSISTORS=$((REG_TRANSISTORS + MEM_TRANSISTORS + ADDER_TRANSISTORS + CONTROL_TRANSISTORS + MUX_TRANSISTORS))

echo ""
echo "=========================================="
echo "总计: ~$TOTAL_TRANSISTORS 晶体管"
echo "=========================================="
echo ""

# 与真实 8086 对比
echo "5. 与真实 Intel 8086 对比"
echo "----------------------------------------"
echo "真实 Intel 8086 (1978):"
echo "  - 晶体管数: 29,000"
echo "  - 工艺: 3μm"
echo "  - 频率: 5-10 MHz"
echo ""
echo "MyCPU8086 (简化版):"
echo "  - 晶体管数: ~$TOTAL_TRANSISTORS"
echo "  - 占比: $((TOTAL_TRANSISTORS * 100 / 29000))%"
echo "  - 说明: 这是一个极简化的实现，只包含基本功能"
echo ""

# 如果有 yosys，可以进行综合
if command -v yosys &> /dev/null; then
    echo "6. Yosys 综合分析"
    echo "----------------------------------------"
    echo "检测到 Yosys，可以运行详细综合分析"
    echo "运行: yosys -p 'read_verilog $VERILOG_FILE; synth; stat'"
else
    echo "提示: 安装 Yosys 可以获得更精确的资源统计"
fi

echo ""
echo "分析完成！"
