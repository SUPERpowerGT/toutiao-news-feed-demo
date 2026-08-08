# 15. Project Deliverables TODO

> 依据根目录 `Project Requirements SE33 v2.pdf` 整理。
>
> 基准日期：2026-08-07。文档中的“已有”仅表示仓库中存在相关材料，不代表已经满足评分标准；所有已有材料仍需经过内容核验、结果截图和演示验证。

## 15.1 使用方式

状态标记：

- `[ ]` 未开始或尚未核验
- `[~]` 已有基础材料，但需要补充或整理
- `[x]` 已完成并具备可提交证据

优先级：

- **P0**：评分明确要求，缺失会直接影响提交完整性或主要评分项
- **P1**：用于证明架构、设计、测试和工程质量
- **P2**：增强展示效果或 Added Value

每项任务只有在同时满足以下条件后才能标记为 `[x]`：

1. 对应文档、代码或配置已进入仓库。
2. 有真实执行结果、截图、日志、报告或视频作为证据。
3. 文档中的描述与当前代码实现一致。
4. 另一名成员按验收要求复核通过。

## 15.2 官方时间节点

| 节点 | PDF 日期 | 当前处理要求 |
|---|---:|---|
| 项目开展周期 | 2026-03-16 至 2026-10-30 | 最终截止日前完成 Phase 2 和最终提交包 |
| 首次月度进度报告 | 2026-05-01 | 已过期；核验是否已提交，并归档历史报告 |
| 第一次展示 | 2026-06-27（暂定） | 已过期；归档幻灯片、录像、反馈和改进项 |
| 第一次提交 | 2026-07-11 | 已过期；核验 Canvas 提交记录和 Phase 0+1 材料 |
| 最终展示 | 2026-10-10（暂定） | 按正式通知复核日期，提前完成彩排 |
| 最终提交 | 2026-10-30 | 硬截止；预留至少 3 天检查压缩包和上传 |

> 日期来自 PDF 的 Part-time 安排。若 Canvas 或 lecturer 的最新通知不同，以最新正式通知为准，并同步更新本表。

## 15.3 第一优先级总清单

建议按以下顺序推进，避免最后只有说明文档、没有可验证证据：

- [ ] **P0-01：建立交付物索引与负责人表**
  - 要求：为全部任务填写负责人、计划完成日、实际完成日、证据链接和复核人。
  - 产物：在本文末尾维护交付索引，或建立独立 tracker。
  - 完成标准：每个 P0/P1 项都有唯一负责人和截止日期，没有“大家共同负责”的模糊项。

- [ ] **P0-02：核验 Phase 0+1 历史提交**
  - 要求：确认第一次展示、第一次提交和月度进度报告是否真实提交成功。
  - 产物：Canvas 提交截图、提交文件副本、展示反馈、未完成项清单。
  - 完成标准：每个历史节点都能给出“已提交证据”或“补救行动与负责人”。

- [ ] **P0-03：建立 Phase 2 Sprint 管理证据**
  - 要求：补齐 Sprint Plan、Sprint Backlog、Burndown Chart、Sprint Review 和 Sprint Retrospective。
  - 产物：至少一个完整 Sprint 的计划、每日/周期更新记录、评审和复盘。
  - 完成标准：backlog、commit/PR、工时和 burn-down 数据能够互相对应。

- [ ] **P0-04：补齐真实自动化测试与结果报告**
  - 要求：实现并运行 Unit、Integration、End-to-End、Stress/Load Testing。
  - 产物：测试代码、执行脚本、终端日志、机器可读报告、关键结果截图。
  - 完成标准：测试可重复运行，失败会返回非零退出码，报告中有环境、时间、用例数、通过率和性能指标。

- [ ] **P0-05：落地 DevSecOps 安全证据**
  - 要求：CI/CD 中加入 SAST、DAST、依赖/容器镜像漏洞扫描，并记录修复与复扫。
  - 产物：pipeline 配置、扫描报告、漏洞处置表、复扫通过证据。
  - 完成标准：每类扫描至少有一次真实结果；High/Critical 问题已修复或有书面风险接受说明。

- [ ] **P0-06：制作最终展示材料与 7 个视频**
  - 要求：覆盖 PDF 指定的管理、架构、设计、DevSecOps、Added Value、App Demo 和 CI/CD Demo。
  - 产物：最终 slides、7 个 1920x1080 视频、演讲稿、演示数据和备份录像。
  - 完成标准：所有视频可播放、命名正确、成员出镜、总内容无重复空洞部分，且每个评分点都有画面证据。

- [ ] **P0-07：组装并验收最终提交包**
  - 要求：代码、仓库 URL、报告、slides、视频和 peer assessment 全部齐备。
  - 产物：按 `<TeamXX-Project Title>.zip` 命名的最终压缩包及 checksum。
  - 完成标准：在另一台机器解压后，链接可访问、视频可播放、代码可构建、文档无缺页。

## 15.4 Phase 0：产品与初始架构

### 15.4.1 产品方向

- [ ] **P0：Product/Platform Roadmap**
  - 要求：展示多个 release，而不是只描述当前 MVP；每个 release 写清目标、核心能力、预计时间和依赖。
  - 至少包含：MVP 推荐流、质量与安全增强、可扩展/智能化能力三个阶段。
  - 证据：roadmap 图或表、版本里程碑、已完成项与计划项状态。
  - 完成标准：roadmap 与 backlog、Sprint 和当前代码状态一致。

- [ ] **P0：完整 Product/Platform Backlog**
  - 要求：覆盖功能、架构、测试、安全、DevOps、文档和演示任务；条目具备优先级、验收标准、估算和负责人。
  - 证据：可导出的 backlog 文件或项目管理工具截图。
  - 完成标准：所有展示功能和评分要求都能追溯到 backlog item。

- [~] **P1：项目目标、痛点、范围与用户旅程**
  - 已有基础：[01-Background&Objectives.md](./01-Background&Objectives.md)、[02-RequirementsAnalysis.md](./02-RequirementsAnalysis.md)。
  - 要求：明确目标用户、业务痛点、in-scope、out-of-scope、成功指标和端到端用户旅程。
  - 证据：一页问题陈述、一页范围表、一张 journey map。
  - 完成标准：目标能够通过演示或指标验证，不使用“提升体验”等不可测表述。

### 15.4.2 初始 Solution Architecture

- [~] **P1：架构约束与 Architecture Decision Records**
  - 已有基础：[04-SystemArchitectureDesign.md](./04-SystemArchitectureDesign.md)。
  - 要求：记录 Android、Go、PostgreSQL、Docker、网络和部署选择的背景、备选方案、取舍与后果。
  - 证据：关键 ADR 列表，每个决策有状态和日期。
  - 完成标准：至少覆盖客户端架构、后端分层、数据库、分页策略、缓存和部署方式。

- [~] **P1：逻辑架构图**
  - 要求：展示 Client、API、Application、Domain、Infrastructure、Database 的边界、职责和依赖方向。
  - 证据：可读的高清图及文字说明。
  - 完成标准：图中组件能映射到真实代码目录，不出现代码中不存在的服务。

- [~] **P1：物理架构与 Deployment Diagram**
  - 要求：展示 Android 设备/模拟器、后端容器、PostgreSQL、网络端口、协议和运行环境。
  - 证据：部署图、Docker Compose 配置、启动与健康检查结果。
  - 完成标准：按图和 README 可在干净环境启动系统。

- [~] **P1：DDD / Microservice Architecture 说明**
  - 已有基础：[08-BackendArchitectureDesign.md](./08-BackendArchitectureDesign.md)。
  - 要求：说明 bounded context、domain model、repository interface、application service 和 infrastructure adapter。
  - 证据：DDD 分层图、核心领域对象及调用链。
  - 完成标准：明确本项目是轻量 DDD 单体还是微服务；不能把普通分层架构错误描述成已实现微服务。

- [~] **P1：技术栈清单**
  - 要求：列出组件、版本、用途、选型原因、license/安全考虑和替代方案。
  - 证据：README/报告中的版本表，构建文件与镜像版本可核对。
  - 完成标准：文档版本与当前 Gradle、Go module、Docker image 一致。

## 15.5 Phase 1/2：Sprint 管理任务

- [ ] **P0：Sprint Plan**
  - 要求：写明 Sprint Goal、周期、容量、成员可用工时、选入故事和风险。
  - 证据：Sprint 启动记录。
  - 完成标准：选入工作量不超过团队容量，且服务于 roadmap milestone。

- [ ] **P0：Sprint Backlog**
  - 要求：每项包含用户故事/任务、优先级、story point 或工时、负责人、状态和验收标准。
  - 证据：起始快照、结束快照和变更记录。
  - 完成标准：代码提交、测试和文档都能关联到任务 ID。

- [ ] **P0：Burndown Chart 与总体投入**
  - 要求：持续记录 remaining effort，展示计划线、实际线和 scope change；汇总每名成员投入时间。
  - 证据：burndown 图、工时表、数据来源说明。
  - 完成标准：曲线数据可追溯，异常波动有解释，不在 Sprint 结束时一次性补录。

- [ ] **P0：Sprint Review**
  - 要求：记录完成/未完成故事、可运行 demo、stakeholder feedback 和 backlog 调整。
  - 证据：review notes、截图或短视频、反馈处理项。
  - 完成标准：每个“完成”故事都通过验收标准并有演示证据。

- [ ] **P0：Sprint Retrospective**
  - 要求：总结做得好的、需要改善的、根因和下一 Sprint 的可执行行动。
  - 证据：retro 文档和 action owner。
  - 完成标准：每个改进行动有负责人、截止日期，下一次 retro 检查结果。

- [ ] **P0：定期 Progress Report**
  - 要求：Part-time 项目按月提交 Canvas；每份包含标题、日期、Sprint backlog/已完成工作、每名成员工时、需要 lecturer 协助的问题和下阶段计划。
  - 证据：PDF/文档副本和 Canvas 提交截图。
  - 完成标准：从 2026-05-01 起的每个要求周期均有报告或书面缺失说明。

- [ ] **P0：Peer Assessment 材料**
  - 要求：按课程指定格式真实记录成员贡献，不在截止日前临时估计。
  - 证据：个人工时、任务、commit/PR、评审和展示贡献记录。
  - 完成标准：评价有可核对事实，所有成员按要求独立提交。

## 15.6 软件设计与数据设计

- [~] **P1：Use Case Diagram**
  - 已有基础：[03-UseCaseAnalysis&UseCaseDiagram.md](./03-UseCaseAnalysis&UseCaseDiagram.md)。
  - 要求：展示 actor、系统边界、主要 use case 及 include/extend 关系。
  - 证据：高清图和用例说明。
  - 完成标准：用例与实际 MVP 演示范围一致。

- [ ] **P0：关键用例分析到设计的可追溯链**
  - 要求：选择重要用例，例如初次加载、下拉刷新、游标加载更多或频道切换；从 use case 追踪到类、sequence、API、数据库和测试。
  - 证据：traceability matrix。
  - 完成标准：每个关键步骤都能指向实现文件和测试用例。

- [ ] **P0：关键用例 Class Diagram**
  - 要求：包含关键类、职责、关系、主要属性/方法和层次边界，不只展示目录结构。
  - 证据：分析类图和设计类图，必要时标注二者演化关系。
  - 完成标准：类名和关系与当前代码一致，文字在演示画面中可读。

- [ ] **P0：关键用例 Sequence Diagram**
  - 要求：覆盖 UI、ViewModel/UseCase、API、Service、Repository 和 Database 的完整交互；包含成功及主要失败/空数据分支。
  - 证据：至少一张端到端时序图。
  - 完成标准：消息顺序、接口名称和返回数据与实现一致。

- [~] **P1：Design Patterns 说明**
  - 要求：只说明代码中真实使用的模式，例如 Repository、Adapter、Factory、MVVM；解释解决的问题和代价。
  - 证据：模式图、代码引用和适用场景。
  - 完成标准：每个模式都有真实实现，不为凑数量而命名。

- [~] **P1：关系型数据库设计**
  - 已有基础：[05-DatabaseDesign.md](./05-DatabaseDesign.md)。
  - 要求：提供 ERD、表/字段、主外键、索引、约束、分页查询和数据生命周期说明。
  - 证据：schema SQL、ERD、关键查询及 explain/性能依据。
  - 完成标准：设计文档与当前 PostgreSQL schema 一致。

- [ ] **P1：NoSQL 适用性说明**
  - 要求：PDF 要求展示 DB 与 NoSQL design/collection objects；若项目未使用 NoSQL，应明确写出“不使用”的决策、原因和未来适用场景。
  - 证据：ADR 或报告小节。
  - 完成标准：评审者无需猜测该项是遗漏还是明确取舍。

- [~] **P1：API 设计核验**
  - 已有基础：[06-APIInterfaceDesign.md](./06-APIInterfaceDesign.md)。
  - 要求：覆盖 endpoint、参数、响应 schema、错误码、分页语义、示例和版本策略。
  - 证据：API 文档和可执行请求结果。
  - 完成标准：示例响应来自当前服务，文档不存在过期字段。

## 15.7 测试任务

> [14-TestingStrategy.md](./14-TestingStrategy.md) 已给出策略，但评分需要真实测试代码和结果 artifact，策略文档不能替代执行证据。

- [ ] **P0：Backend Unit Tests**
  - 要求：覆盖 feed service、场景过滤、刷新、游标分页、推荐理由和推荐分数等核心逻辑；隔离真实数据库。
  - 证据：`*_test.go`、覆盖率报告和 CI 结果。
  - 完成标准：`go test ./...` 稳定通过；核心业务包含正常、边界和错误用例。

- [ ] **P0：Android Unit Tests**
  - 要求：覆盖 ViewModel/UseCase 的 initial load、refresh、no-new-items、load more、scene switch 和错误恢复。
  - 证据：非 ignored 的测试、测试报告和 CI 结果。
  - 完成标准：测试断言真实 UI state transition，不保留只有模板意义的测试作为主要证据。

- [ ] **P0：Integration Tests**
  - 要求：使用真实 PostgreSQL 测试 API、service、repository、schema 和 cursor pagination 的协作。
  - 证据：可重复初始化的测试数据库、脚本、请求/响应断言和报告。
  - 完成标准：环境可一键建立和清理；重复运行结果一致，不依赖手工预置状态。

- [ ] **P0：End-to-End Tests**
  - 要求：验证启动系统、seed、Android 初次加载、下拉刷新、加载更多和频道切换的主要用户路径。
  - 证据：自动化测试优先；若部分只能手测，提供步骤、期望结果、实际结果、截图/录像和执行人。
  - 完成标准：至少一条关键路径从客户端到数据库完整通过，并记录失败恢复行为。

- [ ] **P0：Stress / Load Tests**
  - 要求：定义负载模型、并发用户、持续时间、数据规模、目标 SLA 和测试环境；测试 feed 和 refresh 等主要接口。
  - 指标：吞吐量、P50/P95/P99 latency、错误率、CPU、内存和数据库连接。
  - 证据：脚本、原始结果、趋势图、瓶颈分析和优化前后对比。
  - 完成标准：结果可重复，结论不只写“系统稳定”，而是与预设 SLA 对照。

- [ ] **P1：测试结果总报告**
  - 要求：汇总测试范围、环境、数据、用例数、通过/失败、覆盖率、缺陷和残余风险。
  - 证据：版本化报告及原始 artifact 链接。
  - 完成标准：slides 中的每个数字都能追溯到报告或 CI run。

## 15.8 DevSecOps 与安全任务

- [ ] **P0：CI/CD Pipeline 与图示**
  - 要求：至少包含 checkout、build、unit test、integration test、security scan、artifact/image build 和 deployment/demo stage。
  - 证据：pipeline 配置、pipeline diagram、成功和失败 run 截图。
  - 完成标准：提交代码后能自动触发；测试或安全门禁失败会阻止后续阶段。

- [ ] **P0：SAST**
  - 要求：对 Go、Kotlin/Android 和仓库配置执行静态安全扫描。
  - 证据：带工具版本和 commit SHA 的报告、发现项、修复 commit 和复扫结果。
  - 完成标准：Critical/High 均已处理；误报有理由和审批记录。

- [ ] **P0：DAST**
  - 要求：对运行中的 API 执行动态扫描，覆盖输入校验、错误信息、常见 Web/API 风险和非预期 endpoint。
  - 证据：扫描配置、目标环境、报告、修复与复扫。
  - 完成标准：扫描对象是当前构建版本，不对未授权的外部系统执行测试。

- [ ] **P0：依赖与容器镜像安全**
  - 要求：扫描 Go module、Gradle dependency、Docker base image 和最终 image；固定关键版本，避免泄露 secret。
  - 证据：SBOM/依赖报告、image scan、secret scan 和处置记录。
  - 完成标准：无未处置 Critical/High 漏洞，镜像来源和版本可追溯。

- [ ] **P1：容器管理与部署可恢复性**
  - 要求：说明 image tagging、health check、resource limit、配置/secret 管理、restart 和 rollback 策略。
  - 证据：Compose/部署配置和一次恢复或回滚演示。
  - 完成标准：环境故障后能按文档恢复，不依赖个人机器上的隐式配置。

- [ ] **P1：日志、监控与审计**
  - 要求：记录请求、错误、关键业务事件和部署信息；避免日志包含密码或敏感数据。
  - 证据：结构化日志样例、监控截图、故障定位示例和 version-control audit trail。
  - 完成标准：能通过日志定位一次演示故障，并追溯到构建版本。

- [ ] **P1：安全风险与缓解方案**
  - 要求：形成 threat model/risk register，至少覆盖明文 HTTP、输入验证、SQL 注入、seed endpoint、凭据、依赖漏洞、数据隐私和拒绝服务。
  - 证据：风险等级、影响、可能性、owner、mitigation、残余风险。
  - 完成标准：高风险有代码/配置层面的解决方案，不只写一般性建议。

- [ ] **P2：IaC / Compliance as Code 适用性**
  - 要求：若采用则展示基础设施配置和自动校验；若不采用则说明项目规模、部署方式和替代控制。
  - 证据：配置、检查结果或 ADR。
  - 完成标准：对 PDF 评分项给出明确证据或明确的范围说明。

## 15.9 Added Value

- [ ] **P1：明确 Added Value 主线**
  - 要求：从 Agentic AI、LLM、ML、analytics、real-time、fault tolerance、safety-critical 等方向中选择与项目目标真正相关的一项，不需要全部实现。
  - 证据：问题、方案、架构、实现、评估指标和对照结果。
  - 完成标准：不仅调用现成 API；能够说明技术贡献、效果和限制。

- [ ] **P1：量化价值与 Sponsor/Stakeholder Acceptance**
  - 要求：定义价值指标，例如推荐相关性、刷新延迟、吞吐量、错误率或用户任务完成率；获取 stakeholder 反馈或验收。
  - 证据：before/after 数据、验收记录、反馈及改进。
  - 完成标准：Added Value 有可测结果，不只出现在未来计划中。

- [ ] **P2：上线或准生产演示**
  - 要求：若条件允许，提供可访问环境或可重复部署流程，并说明安全边界和成本。
  - 证据：部署记录、健康检查、监控和回滚方案。
  - 完成标准：展示环境稳定，且没有暴露测试凭据或危险管理接口。

## 15.10 第一次展示与第一次提交归档

- [ ] **P0：第一次展示 Slides 完整性核验**
  - 要求：应覆盖项目介绍、用例图、roadmap/milestones、完整 backlog、Sprint effort、技术栈、架构约束与决策、逻辑/物理/部署架构、DDD/微服务说明、软件设计、DB/NoSQL、CI/CD、MVP demo、unit/integration/E2E/stress test、管理/技术/安全问题与缓解。
  - 证据：最终 slides 和展示录像。
  - 完成标准：建立“PDF 要求 -> slide 页码 -> 证据”映射表，无空项。

- [ ] **P0：第一次提交材料归档**
  - 要求：Presentation slide、App/CI-CD/Testing/Security 等 4-5 分钟 demo videos、Phase 0+1 综合报告、code artifacts、repository URL、team peer assessment。
  - 视频分辨率：PDF 接受 800x600 或 1920x1080；建议统一使用 1920x1080。
  - 证据：提交包、Canvas 提交记录和 checksum。
  - 完成标准：材料可解压、可播放、URL 可访问，并与当时提交版本一致。

## 15.11 最终展示任务

- [ ] **P0：最终 Slides**
  - 要求：以评分 rubric 为目录，覆盖 Management、Architecture、Technical Design、DevSecOps、Added Value 和 Demo；说明 Phase 1 反馈如何在 Phase 2 落地。
  - 证据：可编辑源文件和 PDF 版本。
  - 完成标准：所有结论附证据页，图中文字在 1920x1080 录屏中清晰可读。

- [ ] **P0：App Live Demo / 备份视频**
  - 要求：演示初始化、推荐频道、其他频道、下拉刷新、无新内容、加载更多、异常/恢复和关键 Added Value。
  - 证据：演示脚本、固定 seed 数据、环境检查脚本和备份录像。
  - 完成标准：按脚本连续运行成功两次；断网或服务失败时有恢复方案。

- [ ] **P0：DevSecOps Live Demo / 备份视频**
  - 要求：展示 commit 触发 pipeline、自动测试、安全扫描、artifact/image、部署与健康检查；最好包含一次门禁失败和修复后通过。
  - 证据：真实 pipeline run URL/截图和本地备份录像。
  - 完成标准：所有画面来自真实执行，不使用只有文字的流程图代替演示。

- [ ] **P0：最终彩排**
  - 要求：所有成员参与，按正式时间限制演练；记录超时、切换、网络、字体和音频问题。
  - 证据：至少两次彩排记录和问题关闭清单。
  - 完成标准：最终一次彩排不超时，演示失败切换到备份视频不超过 30 秒。

## 15.12 最终提交：7 个指定视频

所有视频统一要求：

- 分辨率为 **1920x1080 HD**。
- 演讲者开启摄像头并在画面中可见。
- 所有成员均有实质贡献和讲解内容。
- 声音清晰，代码/图表文字可读，无通知、密码或个人敏感信息入镜。

- [ ] **Video 1：`TeamXX- Management Assessment.mp4`**
  - 内容：目标、痛点、范围、journey map、roadmap、backlog、Scrum/Sprint、burndown、成员投入、管理问题与缓解。
  - 完成标准：展示真实 tracker 和趋势数据，不只复述流程定义。

- [ ] **Video 2：`TeamXX- Architectural Assessment.mp4`**
  - 内容：逻辑架构决策与总览、DDD、部署图、物理架构决策、技术栈、基础设施/网络和安全架构。
  - 完成标准：架构图能映射到代码与部署配置，并解释关键取舍。

- [ ] **Video 3：`TeamXX- Technical Assessment - Software Design.mp4`**
  - 内容：用例图、关键用例 class/sequence diagram、design patterns、数据 schema/model 和实现追踪。
  - 完成标准：至少沿一条关键用例从设计讲到代码和测试。

- [ ] **Video 4：`TeamXX- Technical Assessment - DevSecOps.mp4`**
  - 内容：CI/CD、unit/integration/load-stress test artifacts、SAST、DAST、容器/镜像安全、日志、漏洞修复与复扫、IaC/compliance 适用性、版本控制审计。
  - 完成标准：关键工具均展示真实结果和 commit/build 标识。

- [ ] **Video 5：`TeamXX- Value Added Assessment.mp4`**
  - 内容：Added Value 的问题、方案、实现、创新点、评估方法、量化结果、限制和 stakeholder acceptance。
  - 完成标准：有可比较数据或验收证据，区分已实现与未来工作。

- [ ] **Video 6：`TeamXX- Presentation Assessment App Demo.mp4`**
  - 内容：完整 App 主流程、关键功能、错误恢复和用户价值。
  - 完成标准：展示真实运行系统，数据和操作路径可重复。

- [ ] **Video 7：`TeamXX- Presentation Assessment CICD Demo.mp4`**
  - 内容：从代码变更到 build、test、scan、artifact/image、deploy 和 health check 的真实流水线。
  - 完成标准：清楚展示一次完整 run，各阶段结果可读。

## 15.13 最终提交包验收

- [ ] **P0：代码与仓库 URL**
  - 要求：提交版本有明确 tag/commit SHA；README 包含依赖、配置、构建、运行、测试和 demo 数据说明。
  - 完成标准：另一名成员按 README 在干净环境完成构建和关键测试。

- [ ] **P0：报告与 Slides**
  - 要求：内容完整、页码/目录正确、引用有效、图表清晰、实现状态真实。
  - 完成标准：所有 rubric 项可通过目录或映射表快速定位。

- [ ] **P0：视频压缩包**
  - 要求：7 个视频名称完全符合指定格式，并压缩为 `<TeamXX-Project Title>.zip`。
  - 完成标准：解压无错误，7 个视频均可从头播放到结尾，音画同步。

- [ ] **P0：最终完整性检查**
  - 要求：检查文件名、分辨率、成员出镜、链接权限、敏感信息、病毒/损坏、压缩包大小和 Canvas 上传限制。
  - 证据：由非打包人执行并签名的 checklist、checksum 和上传成功截图。
  - 完成标准：至少在截止前 72 小时生成候选包，在截止前 24 小时完成最终上传核验。

## 15.14 评分权重与投入建议

| 评分项 | 权重 | 执行建议 |
|---|---:|---|
| First Presentation | 10% | 归档反馈，并在最终材料中展示改进闭环 |
| Second Presentation | 10% | 用真实 demo 和证据组织叙事，提前彩排 |
| Project Deliverables（含 demonstration） | 45% | 最高优先级；保证报告、代码、测试、安全和视频完整一致 |
| Client Feedback | 15% | 主动安排验收，保存书面反馈及改进结果 |
| Peer Assessments | 20% | 持续维护个人贡献证据，避免截止日前补记 |

## 15.15 当前仓库初步差距

| 领域 | 当前基础 | 下一步 |
|---|---|---|
| 需求、用例、架构、数据库、API | 已有多份 `docs/` 设计文档 | 对照当前代码核验，补高清图、ADR 和追踪矩阵 |
| DDD | 已有轻量 DDD 后端说明 | 明确单体边界，补领域图及代码映射 |
| 测试 | 已有测试策略和执行脚本草案 | 增加真实业务测试，保留可提交结果 artifact |
| Sprint 管理 | 未发现完整 roadmap/backlog/burndown/review/retro 证据 | 优先补齐 Phase 2，并归档历史材料 |
| DevSecOps | 文档中有 CI/CD 描述 | 落地 pipeline、SAST、DAST、image scan、漏洞复扫证据 |
| 安全 | 开发日志中有网络配置记录 | 建立 threat model/risk register，处理高风险项 |
| Added Value | 尚需确定一条清晰主线 | 选择可在截止前完成且能量化评估的方向 |
| 最终视频 | 尚未发现 7 个规定成片 | 先写脚本和证据映射，再录制和彩排 |

## 15.16 交付物索引模板

执行时为每个 P0/P1 任务增加一行：

| Task ID | Owner | Due Date | Status | Evidence / File | Reviewer | Review Date |
|---|---|---:|---|---|---|---:|
| 示例：P0-04 Backend Unit Tests | 待分配 | YYYY-MM-DD | `[ ]` | 待补充 | 待分配 | - |

## 15.17 推荐倒排计划

| 时间 | 目标 |
|---|---|
| 2026-08-10 前 | 确认团队成员、负责人、历史提交状态、Added Value 方向和正式日期 |
| 2026-08-31 前 | 完成 roadmap/backlog、Phase 2 Sprint 证据、架构与设计缺口清单 |
| 2026-09-15 前 | 完成主要 unit/integration/E2E/stress tests 和第一版结果报告 |
| 2026-09-30 前 | 完成 CI/CD、安全扫描、漏洞修复复扫和 Added Value 量化评估 |
| 2026-10-05 前 | 完成 slides、7 个视频脚本、App/CI-CD 演示脚本和候选录像 |
| 2026-10-10 前 | 完成最终展示彩排与展示；日期以正式通知为准 |
| 2026-10-20 前 | 完成 7 个最终视频和报告定稿 |
| 2026-10-27 前 | 生成并复核最终压缩包，预留上传缓冲 |
| 2026-10-30 前 | 完成最终提交并保存成功凭证 |

