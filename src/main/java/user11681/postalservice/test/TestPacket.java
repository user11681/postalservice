package user11681.postalservice.test;

import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import user11681.postalservice.ThePostalService;

public enum TestPacket implements PacketConsumer {
    instance;

    public static final Identifier identifier = new Identifier("postalservice", "test");

    @Override
    public void accept(final PacketContext context, final PacketByteBuf buffer) {
        System.out.println((Object) ThePostalService.readObject(buffer));
    }
}
