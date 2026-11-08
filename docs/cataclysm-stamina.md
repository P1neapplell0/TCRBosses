# 灾变原版 Boss 耐力与处决

## 范围与启用

为灾变的八个原版 Boss 添加轻量 Epic Fight Patch，不替换其模型、渲染器或正常战斗 AI，也不修改本模组已有的人形 Boss。
关闭功能开关时保留原有 Patch（如果存在）及 Epic Fight 的全局眩晕规则。配置合并不改变已有参数的代码默认值；精简示例将全部 Boss 关闭。

模组启动时，Forge 自动生成完整的 COMMON 配置：

- 单人：游戏根目录下的 `config/tcr_bosses-common.toml`。
- 专用服务器：服务器根目录下的 `config/tcr_bosses-common.toml`。
- 整合包预设：直接将配置放入 `config/tcr_bosses-common.toml`，所有存档共用。

`examples/tcr_bosses-common.toml` 提供全部 21 个 Boss 全关的精简模板，缺少的参数由 Forge 自动补齐。
这是 COMMON 配置，不会自动同步至客户端；客户端与服务端请保持配置一致。修改开关或属性后，请重启游戏或服务器，以重新创建实体 Patch。
所有配置现已合并，原有 `[general]` 分类不变。旧的 `tcr_bosses-cataclysm.toml` 不再读取；请在游戏/服务器关闭时，将其中 `[cataclysm.*]` 各节追加或合并到 `config/tcr_bosses-common.toml`，不要覆盖原有 `[general]` 或重复定义同名节。旧版 SERVER 配置也可按相同方式迁移；没有自动迁移。

例如，只开启焰魔：

```toml
[cataclysm.ignis]
enabled = true

[cataclysm.ignis.stamina]
hurtImpactMultiplier = 1.0
vanillaHitCost = 0.5
attackCost = 1.0
blockedAttackCost = 4.0
regenDelayTicks = 60
breakTicks = 80
recoverTicks = 60

[cataclysm.ignis.execution]
enabled = true

[cataclysm.ignis.epic_fight_attributes]
maxStamina = 80.0
staminaRegen = 1.0
impact = 2.0
armorNegation = 0.0
stunArmor = 20.0
weight = 800.0
maxStrikes = 10
executionResistance = 1
```

## 配置分类

每个 `cataclysm.<boss>` 独立包含：

| 分类 | 参数 | 含义 |
| --- | --- | --- |
| 根节点 | `enabled` | 该 Boss 总开关；精简示例全部关闭 |
| `stamina` | `hurtImpactMultiplier` | 成功受击后，Epic Fight 冲击力转化为耐力损失的倍率 |
| `stamina` | `vanillaHitCost` | 非 Epic Fight 攻击的基础耐力损失，也乘以上述倍率 |
| `stamina` | `attackCost` | 切入新原生动画/招式时消耗的耐力；0 为关闭 |
| `stamina` | `blockedAttackCost` | 攻击被 Epic Fight 格挡/弹反后额外消耗的耐力 |
| `stamina` | `regenDelayTicks` | 最后一次行动或受击后的自然恢复等待时间 |
| `stamina` | `breakTicks` | 无处决时的破防窗口时长 |
| `stamina` | `recoverTicks` | 破防/处决结束后的线性回满时长 |
| `execution` | `enabled` | 是否允许 CombatEvolution 处决，还需开启该 Boss 总开关 |
| `epic_fight_attributes` | `maxStamina`, `staminaRegen` | 最大耐力与自然恢复速度 |
| `epic_fight_attributes` | `impact`, `armorNegation`, `maxStrikes` | 冲击力、护甲穿透百分比、最大打击数 |
| `epic_fight_attributes` | `stunArmor`, `weight` | 眩晕护甲、重量 |
| `epic_fight_attributes` | `executionResistance` | Epic Fight 原生即死处决抗性，不是 CombatEvolution 伤害倍率 |

20 tick = 1 秒。属性只在总开关启用时写入实体；灾变原生招式仍走其原生伤害计算，不会因为配置了 Epic Fight 冲击力而自动变成 Epic Fight 攻击。

## 耐力流程

1. 入场时满耐力。成功造成生命伤害的攻击按冲击力扣除耐力；无效攻击、无敌阶段、环境伤害的零伤害不会扣除。
2. 新的原生招式开始时可额外扣除 `attackCost`。正在行动时暂停自然恢复。
3. 行动结束或受击后等待 `regenDelayTicks`；随后每 tick 恢复 `最大耐力 × 0.01 × staminaRegen`，参考 CombatEvolution 的比例恢复方式。
4. 耐力耗尽进入 BREAK，停止原生 AI 和招式脚本，播放灾变自带的反应动画。焰魔与斯库拉在破防/被处决时不能继续盾挡或弹反。
5. 玩家使用 CombatEvolution 原本的处决键、武器与处决动画。处决资格由服务端验证，只在 BREAK 窗口内成立；继续遵守 CombatEvolution 的全局开关、武器限制、视线与站位检查。
6. 处决期间保持破防，不提前恢复；完成后立即进入 RECOVER，防止同一破防窗口反复处决。未处决则在 `breakTicks` 后进入 RECOVER。
7. RECOVER 内按 `recoverTicks` 线性回满耐力，不再损失耐力、不可处决；生命仍可受到伤害。结束后可再次破防。

禁用普通 Epic Fight 受击动画，以免把非人形 Boss 拉成人形姿态。处决只负责衔接 CombatEvolution 的攻击与伤害，不保证一击击杀，灾变自己的伤害上限与其他免疫规则仍保留。

## 原生动画映射

| Boss 配置键 | 破防 | 被处决 |
| --- | --- | --- |
| `ender_guardian` | `GUARDIAN_FALLEN` | `GUARDIAN_FALLEN` |
| `netherite_monstrosity` | 原生 `death` 状态（5） | 原生 `death` 状态（5） |
| `ignis` | `BREAK_THE_SHIELD` | `BREAK_THE_SHIELD` |
| `the_harbinger` | `STUN_ANIAMATION` | `STUN_ANIAMATION` |
| `the_leviathan` | `LEVIATHAN_STUN` | `LEVIATHAN_STUN` |
| `ancient_remnant` | 原生 `charge_stun` 状态（12） | 同左 |
| `maledictus` | 原生落地状态（5） | 原生 `death` 状态（6） |
| `scylla` | 原生 `parry` 状态（19） | 原生 `death` 状态（12） |

缺少专用眩晕动画的 Boss 使用原生倒下/死亡动作作为非致死倒地表现，不会直接杀死实体。动画期间跳过具体 Boss 的攻击/阶段脚本，避免借用反应动作时错误触发攻击、永久破盾或阶段变化；生命归零时仍走灾变本来的死亡流程。

## 血条与兼容

耐力条绑定原生生命 BossEvent UUID，不按名字猜测实体；因此同名 Boss、多 Boss、远离实体后的血条都不会串条，也不会错误画到怒气条上。
保持灾变原本的生命条、边框与名字，在装饰下方追加细耐力条并增加排版间距。颜色参考各 Boss 原生命条；焰魔转入魂火阶段后随原血条变为青色。关闭灾变自定义血条时，仍在原版血条下显示。

需要 Epic Fight、Indestructible、Cataclysm、CombatEvolution 2.1.8 或更高的兼容版本。服务端与客户端均须安装本模组。
外部数据包如果为同一实体强制注册自定义 Patch，可能覆盖代码注册的 Patch；这种情况下应移除重复覆盖，不能同时使用两套实体 Patch。
