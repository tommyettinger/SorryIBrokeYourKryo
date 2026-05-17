package com.github.tommyettinger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Version;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.notgdx.utils.Array;
import com.badlogic.gdx.utils.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.VersionFieldSerializer;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import org.objenesis.strategy.StdInstantiatorStrategy;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture image;

    private Kryo kryo;


    @Override
    public void create() {
        // Strongly recommended in this case to avoid storing anything in the problem fields!
        Collections.allocateIterators = true;

        // However you normally initialize Kryo, do it here.
        kryo = new Kryo();
        kryo.setDefaultSerializer(VersionFieldSerializer.class);
        kryo.register(Array.class);
        kryo.register(Object[].class); // This seems to be needed by OrderedMap?
        kryo.register(OrderedMap.class);
        kryo.register(OrderedSet.class);
        ((DefaultInstantiatorStrategy)kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());

        // Creates an OrderedMap for testing.
        OrderedMap<String, Integer> data = new OrderedMap<>();
        data.put("Cthulhu", -123456);
        data.put("lies", Integer.MIN_VALUE);
        data.put("deep", 456789012);
        data.put("in", 0);
        data.put("Rl'yeh", 1111);
        data.put("dreaming", 1);
        data.put("of", -1);
        data.put("waffles", 0);

        // Here we try to save and reload the OrderedMap.
        byte[] saved = save(data);
        OrderedMap<?, ?> loaded = load(saved);

        // Check if the saved and loaded OrderedMaps are equal.
        if(loaded.equals(data)) {
            // They are equal! Save either "1.12.1.save" or "1.13.1.save" depending on libGDX version.
            Gdx.files.local(Version.VERSION + ".save").writeBytes(saved, false);
            System.out.println("YAY! THEY ARE EQUAL!");
        } else {
            System.out.println("NO! THEY DIDN'T WRITE OR READ CORRECTLY!");
        }

        FileHandle oldSave = Gdx.files.local("1.12.1.save");
        FileHandle newSave = Gdx.files.local("1.13.1.save");
        if(oldSave.exists() && newSave.exists()) {
            OrderedMap<?, ?> older = load(oldSave.readBytes());
            OrderedMap<?, ?> newer = load(newSave.readBytes());
            if (older.equals(newer)) {
                System.out.println("HOORAY! Everything works.");
            } else {
                System.out.println("NOOOO!");
            }
        }

        // Just to show something on the screen (default template code).
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        batch.begin();
        batch.draw(image, 140, 210);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }

    /**
     * Convenience wrapper to save a single OrderedMap to a byte array.
     * You probably want to avoid using this in practice, and you would likely want
     * to manage your Output value yourself.
     *
     * @param data any libGDX OrderedMap
     * @return a byte array serializing the given data
     */
    public byte[] save(OrderedMap<?, ?> data){
        Output output = new Output(32, -1);
        kryo.writeObject(output, data);
        return output.toBytes();
    }

    /**
     * Convenience wrapper to load an OrderedMap from a byte array that serialized just an OrderedMap with Kryo.
     * You probably want to avoid using this in practice.
     * This uses a try-with-resources block to handle an Input value.
     *
     * @param bytes a byte array typically produced by {@link #save(OrderedMap)}
     * @return the OrderedMap that was saved with {@link #save(OrderedMap)} originally
     */
    public OrderedMap<?, ?> load(byte[] bytes){
        OrderedMap<?, ?> data;
        try (Input input = new Input(bytes)) {
            data = kryo.readObject(input, OrderedMap.class);
        }
        return data;
    }
}
