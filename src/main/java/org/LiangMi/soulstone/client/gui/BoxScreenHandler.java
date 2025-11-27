package org.LiangMi.soulstone.client.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.LiangMi.soulstone.Soulstone;

// 自定义箱子屏幕处理器类，继承自 Minecraft 的 ScreenHandler
public class BoxScreenHandler extends ScreenHandler {
    // 箱子物品栏实例
    private final Inventory inventory;

    // 客户端使用的构造器 - 当服务器要求客户端开启 screenHandler 时调用
    // 如有空的物品栏，客户端会调用其他构造器，screenHandler 将会自动
    // 在客户端将空白物品栏同步给物品栏。
    public BoxScreenHandler(int syncId, PlayerInventory playerInventory) {
        // 调用主构造器，创建一个包含 9 个槽位的简单物品栏
        this(syncId, playerInventory, new SimpleInventory(9));
    }

    // 服务器端使用的构造器 - 在服务器的 BlockEntity 中被调用
    // 这个构造器是在服务器的 BlockEntity 中被调用的，无需先调用其他构造器，服务器知道容器的物品栏
    // 并直接将其作为参数传入。然后物品栏在客户端完成同步。
    public BoxScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        // 调用父类构造器，传入自定义的屏幕处理器类型和同步ID
        super(Soulstone.BOX_SCREEN_HANDLER, syncId);
        // 检查物品栏大小是否符合预期（9个槽位）
        checkSize(inventory, 9);
        // 初始化物品栏
        this.inventory = inventory;
        // 当玩家开启容器时，执行自定义逻辑
        inventory.onOpen(playerInventory.player);

        // 这会将槽位放置在 3×3 网格的正确位置中。这些槽位在客户端和服务器中都存在！
        // 但是这不会渲染槽位的背景，这是 Screens 类的工作
        int m;  // 行计数器
        int l;  // 列计数器

        // 添加箱子物品栏的槽位 (3x3 网格)
        // Our inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 3; ++l) {
                // 计算槽位索引：l + m * 3 (从左到右，从上到下)
                // 设置槽位位置：x = 62 + l * 18, y = 17 + m * 18
                this.addSlot(new Slot(inventory, l + m * 3, 62 + l * 18, 17 + m * 18));
            }
        }

        // 添加玩家主物品栏的槽位 (3x9 网格)
        // 玩家物品栏
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                // 计算槽位索引：l + m * 9 + 9 (跳过快捷栏的9个槽位)
                // 设置槽位位置：x = 8 + l * 18, y = 84 + m * 18
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }

        // 添加玩家快捷栏的槽位 (1x9 网格)
        // 玩家快捷栏
        for (m = 0; m < 9; ++m) {
            // 快捷栏槽位索引：0-8
            // 设置槽位位置：x = 8 + m * 18, y = 142
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    // 快速移动物品的方法（Shift+点击）
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // 返回 null 表示暂未实现快速移动逻辑
        // 在实际应用中，这里应该包含物品在容器间快速转移的逻辑
        return null;
    }

    // 检查玩家是否可以使用该容器
    @Override
    public boolean canUse(PlayerEntity player) {
        // 委托给物品栏的 canPlayerUse 方法
        return this.inventory.canPlayerUse(player);
    }
}
