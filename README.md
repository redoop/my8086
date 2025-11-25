# My8086 - Chisel 8086 CPU 实现

这是一个使用 Chisel HDL 实现的简化版 8086 CPU。

## 功能特性

- 16位数据总线
- 20位地址总线
- 基本寄存器组（AX, BX, CX, DX, SP, BP, SI, DI）
- 段寄存器（CS, DS, SS, ES）
- 简化的指令集
- 内存接口

## 运行模拟

```bash
# 运行测试
sbt test

# 生成 Verilog 代码
sbt "runMain cpu8086.Main"
```

## 项目结构

```
.
├── build.sbt                          # SBT 构建配置
├── src/
│   ├── main/scala/cpu8086/
│   │   ├── CPU8086.scala             # CPU 实现
│   │   └── Main.scala                # Verilog 生成器
│   └── test/scala/cpu8086/
│       └── CPU8086Test.scala         # 测试用例
└── generated/                         # 生成的 Verilog 代码
```

## 实现的指令

### ⭐ Milestone 1 MVP (v0.2.0) - 当前版本

MyCPU8086_MVP 实现了 **15 条指令**（占 8086 全部 104 条指令的 14.4%）：

**数据传送** (5条):
- ✅ `MOV AX/BX/CX/DX, imm` (0xB8-0xBB): 立即数到寄存器
- ✅ `MOV reg, reg` (0x89): 寄存器间传送

**算术运算** (9条):
- ✅ `ADD reg, reg` (0x01): 加法
- ✅ `SUB reg, reg` (0x29): 减法
- ✅ `INC AX/BX/CX/DX` (0x40-0x43): 寄存器加1
- ✅ `DEC AX/BX/CX/DX` (0x48-0x4B): 寄存器减1
- ✅ `CMP reg, reg` (0x39): 比较

**控制转移** (1条):
- ✅ `JMP rel8` (0xEB): 相对短跳转

**处理器控制** (1条):
- ✅ `HLT` (0xF4): 停止 CPU

📋 **完整指令集清单**: [docs/INSTRUCTION_SET_CHECKLIST.md](docs/INSTRUCTION_SET_CHECKLIST.md)  
🎯 **Milestone 1 报告**: [docs/MILESTONE1_MVP.md](docs/MILESTONE1_MVP.md)

## 寄存器

- **通用寄存器**: AX, BX, CX, DX
- **指针和索引**: SP, BP, SI, DI
- **段寄存器**: CS, DS, SS, ES
- **指令指针**: IP
- **标志寄存器**: FLAGS

## 文档

### 📚 核心文档

- **[快速入门指南](docs/QUICKSTART.md)** - 项目快速上手指南
  - 环境要求和安装
  - 运行测试和生成 Verilog
  - 项目结构说明
  - 常见问题解答

- **[指令集实现清单](docs/INSTRUCTION_SET_CHECKLIST.md)** - Intel 8086 完整指令集对照表
  - 104 条 8086 指令完整列表
  - MyCPU8086 实现状态标注
  - 按类别分类（数据传送、算术、逻辑等）
  - 实现路线图和优先级建议

- **[测试文档](docs/TESTING.md)** - 测试套件说明
  - 3 个测试套件，7 个测试用例
  - 测试运行方法
  - CPU 状态机和时序分析
  - 与 simple_program.md 的对应关系

- **[晶体管数量分析](docs/TRANSISTOR_COUNT.md)** - 硬件资源分析
  - Yosys 综合统计结果
  - 约 100,658 个晶体管（含 1KB SRAM）
  - 核心逻辑 2,354 个晶体管
  - 与真实 Intel 8086 的对比

- **[Milestone 1 MVP 报告](docs/MILESTONE1_MVP.md)** - 最小可用系统完成报告 ⭐ 新增
  - 从 3 条指令扩展到 15 条指令
  - 完整的寄存器组和标志位系统
  - 6 个新测试用例
  - 示例程序和性能分析

### 📖 示例和工具

- **[示例程序](examples/simple_program.md)** - 8086 汇编示例
  - 简单的汇编程序示例
  - 机器码表示
  - 执行流程说明
  - 寄存器详细说明

- **[设计分析脚本](scripts/analyze_design.sh)** - 自动化分析工具
  - 寄存器和内存统计
  - 晶体管数量估算
  - Yosys 综合集成

## 技术栈

- **硬件描述语言**: Chisel 3.6.0
- **构建工具**: SBT 1.9.7
- **测试框架**: ChiselTest 0.6.2
- **综合工具**: Yosys (可选)
- **语言**: Scala 2.13.10

## 项目统计

| 指标 | 初始版本 | Milestone 1 MVP |
|------|---------|----------------|
| 指令实现 | 3/104 (2.9%) | **15/104 (14.4%)** |
| 寄存器支持 | 1 个 (AX) | **4 个 (AX, BX, CX, DX)** |
| 标志位 | 1 个 (ZF) | **5 个 (CF, ZF, SF, OF, PF)** |
| 测试用例 | 7 个 | **13 个** |
| 晶体管数 | ~100,658 (含 1KB SRAM) | ~100,658 (含 1KB SRAM) |
| 核心逻辑 | ~2,354 晶体管 | ~3,500 晶体管 (估算) |
| 文档页面 | 6 个 | **7 个** |
| 代码行数 | ~500 行 Scala | **~850 行 Scala** |

## 开发路线图

### ✅ Milestone 1: 最小可用系统 (MVP) - 已完成
**目标**: 能运行简单的计算程序  
**指令数**: 15 条  
**状态**: ✅ 完成 (2025-11-25)

- [x] 实现 BX, CX, DX 寄存器
- [x] MOV reg, reg
- [x] MOV reg, imm (8位)
- [x] ADD/SUB reg, reg
- [x] INC/DEC 指令
- [x] CMP 指令
- [x] JMP 指令
- [x] 完整的标志位系统

📄 **详细报告**: [docs/MILESTONE1_MVP.md](docs/MILESTONE1_MVP.md)

### 🔄 Milestone 2: 基础功能完整 (进行中)
**目标**: 支持基本的程序控制流  
**指令数**: ~40 条  
**预计完成**: 2025-12

- [ ] 条件跳转 (JE, JNE, JG, JL 等)
- [ ] PUSH/POP 指令
- [ ] CALL/RET 指令
- [ ] 逻辑运算 (AND, OR, XOR, NOT)
- [ ] 内存操作 (MOV reg, mem)

### 📅 Milestone 3: 标准兼容 (计划中)
**目标**: 兼容大部分 8086 程序  
**指令数**: ~80 条  
**预计完成**: 2026-Q1

- [ ] 乘除法指令
- [ ] 字符串操作
- [ ] 中断支持
- [ ] 完整的寻址模式

详细路线图请参考 [指令集实现清单](docs/INSTRUCTION_SET_CHECKLIST.md)

## 贡献

欢迎贡献！如果你想添加新功能或修复 bug：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目仅用于教育和学习目的。

## 致谢

- Intel 8086 处理器设计
- Chisel HDL 开发团队
- UC Berkeley EECS 部门
