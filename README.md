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

MyCPU8086 当前实现了 **3 条指令**（占 8086 全部 104 条指令的 2.9%）：

- ⚠️ `MOV AX, immediate` (0xB8): 将立即数加载到 AX（部分实现）
- ⚠️ `ADD AX, BX` (0x01): AX = AX + BX（部分实现）
- ✅ `HLT` (0xF4): 停止 CPU（完全实现）

📋 **完整指令集清单**: [docs/INSTRUCTION_SET_CHECKLIST.md](docs/INSTRUCTION_SET_CHECKLIST.md)

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

| 指标 | 数值 |
|------|------|
| 指令实现 | 3/104 (2.9%) |
| 晶体管数 | ~100,658 (含 1KB SRAM) |
| 核心逻辑 | ~2,354 晶体管 |
| 测试用例 | 7 个 (100% 通过) |
| 文档页面 | 6 个 |
| 代码行数 | ~500 行 Scala + ~300 行 Verilog |

## 开发路线图

### Phase 1: 基础寄存器和数据传送 (计划中)
- [ ] 实现 BX, CX, DX 寄存器
- [ ] 完整的 MOV reg, reg
- [ ] 完整的 MOV reg, imm (16位)
- [ ] MOV reg, mem / MOV mem, reg

### Phase 2: 基础算术运算 (计划中)
- [ ] 完整的 ADD/SUB 指令
- [ ] INC/DEC 指令
- [ ] CMP 指令
- [ ] 完整的标志位更新

### Phase 3: 基础控制转移 (计划中)
- [ ] JMP 指令
- [ ] 条件跳转 (JE, JNE, JG, JL)
- [ ] CALL/RET 指令
- [ ] PUSH/POP 指令

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
