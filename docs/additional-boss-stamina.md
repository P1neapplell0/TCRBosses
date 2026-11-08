# BOMD、Mowzie's Mobs 与 Block Factory's Bosses 耐力兼容

## 配置位置

所有 Boss 与原有通用设置统一使用 COMMON 配置，启动后在游戏或服务器根目录生成 `config/tcr_bosses-common.toml`：

| 模组 | 顶层分类 | Boss 数量 |
| --- | --- | --- |
| Bosses of Mass Destruction | `bosses_of_mass_destruction` | 4 |
| Mowzie's Mobs | `mowziesmobs` | 4 |
| Block Factory's Bosses | `block_factorys_bosses` | 5 |

配置合并保留已有参数的代码默认值。各模组可以分别安装，不要求三个同时存在；未安装模组不注册实体 Patch。
配置在同一游戏实例的所有存档之间共用，不会自动同步；联机双方需要保持一致，修改后重启游戏或服务器。
`examples/tcr_bosses-common.toml` 提供 21 个 Boss 全关的精简预设，缺失参数由 Forge 补齐。已有配置请合并需要的分类，不要直接覆盖。
灾变的 `[cataclysm.*]` 和原有 `[general]` 也在同一文件内，分类和参数名不变。
旧的 `tcr_bosses-cataclysm.toml`、`tcr_bosses-bomd.toml`、`tcr_bosses-mowziesmobs.toml`、`tcr_bosses-block_factorys_bosses.toml` 不再读取。请关闭游戏/服务器后，将旧文件各分类合并到 `config/tcr_bosses-common.toml`；不要重复定义同名节或覆盖原有 `[general]`。没有自动迁移。

每个 `[模组ID.实体ID]` 下保留相同的三类参数：

- 根节点 `enabled`：该 Boss 的兼容总开关。
- `stamina`：受击冲击力倍率、普通伤害基础扣除、行动与被格挡的消耗、恢复延迟、破防和回满时间。
- `execution`：是否允许在破防窗口内使用 CombatEvolution 处决。
- `epic_fight_attributes`：最大耐力、耐力恢复、冲击力、护甲穿透、眩晕护甲、重量、最大打击数、原生处决抗性。

完整参数含义与计算方式见 `cataclysm-stamina.md` 的“配置分类”和“耐力流程”。
新增 Boss 默认行动消耗为 `1.0`，自然恢复等待 `60 tick`，破防窗口 `80 tick`，恢复期 `60 tick`。
例如，仅开启铁傀儡首领：

```toml
[mowziesmobs.ferrous_wroughtnaut]
enabled = true

[mowziesmobs.ferrous_wroughtnaut.stamina]
hurtImpactMultiplier = 1.0
vanillaHitCost = 0.5
attackCost = 1.0
blockedAttackCost = 4.0
regenDelayTicks = 60
breakTicks = 80
recoverTicks = 60

[mowziesmobs.ferrous_wroughtnaut.execution]
enabled = true

[mowziesmobs.ferrous_wroughtnaut.epic_fight_attributes]
maxStamina = 90.0
staminaRegen = 1.0
impact = 3.0
armorNegation = 0.0
stunArmor = 20.0
weight = 1000.0
maxStrikes = 10
executionResistance = 1
```

## 实体与原生动画

| 模组 | 实体 ID | 破防/被处决使用的原模组动画 |
| --- | --- | --- |
| BOMD | `lich` | `rage_mode`，保持反应姿态 |
| BOMD | `gauntlet` | `punch_stop`，停止出拳并保持姿态 |
| BOMD | `obsidilith` | `death`，使用原生倒下动作，不触发实体死亡逻辑 |
| BOMD | `void_blossom` | `death`，使用原生收拢动作，不触发实体死亡逻辑 |
| Mowzie | `ferrous_wroughtnaut` | `HURT_ANIMATION`，保持受击姿态 |
| Mowzie | `frostmaw` | `LAND_ANIMATION`，保持落地伏低姿态；其 `HURT_ANIMATION` 长度为零，因此不采用 |
| Mowzie | `umvuthi` | `hurt` |
| Mowzie | `sculptor` | `hurt` |
| Block Factory | `yeti` | `animation.get_hit` |
| Block Factory | `infernal_dragon` | 变身前 `animation.hit`，变身后 `animation.dragon.hit_overlay` |
| Block Factory | `underworld_knight` | `animation.knocked_down` |
| Block Factory | `sandworm` | `animation.block_factory.bosses_rise.sandworm.knocked_down` |
| Block Factory | `kraken` | `stun_fall` → `stun_idle` |

小怪、召唤物、海怪触手和过场实体不单独增加耐力条；海怪破防时同时停止其触手攻击。
破防与被处决均使用这些原生动作，开始处决时重新播放，不将巨型 Boss 强行渲染成人形。
没有合适专用眩晕动作的实体使用上述原生受击、收招或倒下动作，并在恢复后撤销姿态。

## 行为与边界

- 不替换原模组的模型、渲染器或正常战斗 AI；关闭功能时保留其他模组注册的 Patch，并继续遵守 Epic Fight 全局眩晕规则。
- 服务端计算耐力、破防窗口及处决资格。沿用 CombatEvolution 的数据同步、处决按键、武器和玩家动画。
- 破防期间冻结 Boss 的目标行为和原生攻击脚本；BOMD 清理实体本地的延迟攻击队列，Mowzie 暂停技能，海怪触手停止继续攻击。
- 已经发射的投射物、独立召唤物和世界级技能效果不会被全局删除。
- 只对成功造成生命伤害的攻击扣除受击耐力；不主动移除正常战斗中的护盾、背后弱点或阶段免疫。铁傀儡仍须通过其原本的弱点机制削减耐力。
- 不打断冰霜巨兽/铁傀儡的唤醒阶段、雪怪的冰封/过场、龙与骑士过场、沙虫隐藏/入场、海怪隐藏/过场/死亡，以及雕刻家的非战斗试炼。
- Block Factory Boss 因实际伤害进入死亡、复活或变身过场时，退出耐力破防并让原生过场优先，不将其强制重置为空闲状态。
- 破防期间铁傀儡等 Boss 不再以正常战斗的方向护甲阻挡处决伤害；恢复后重新保留原来的防御机制。
- 耐力条绘制在原版或模组自定义 Boss 血条下方，使用相近的血条颜色。Block Factory 的血条设置为 OFF 时也不显示耐力条。
- 最大耐力与其他 Epic Fight 属性只在开关启用时写入；原模组招式不会仅因配置了冲击力而自动变成 Epic Fight 攻击。

## 本地验证

依赖使用提供的三个 JAR：BOMD Forge `1.1.2`、Mowzie's Mobs `1.8.2`、Block Factory's Bosses `2.1.2`，以及 BOMD 所需的 Cerbons API `1.1.0`。
构建需要 Java 17，运行 `bash ./gradlew clean build --offline`。
专用服务器的隔离验证与客户端目视验证是不同步骤；客户端仍需检查自定义血条布局、原生姿态和实际玩家处决效果。
