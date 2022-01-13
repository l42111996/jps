package org.ksdev.jps.bit;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.ksdev.jps.test.Hex;
import org.ksdev.jps.test.OffsetCoord;

import java.io.*;

/**
 * Created by JinMiao
 * 2022/1/12.
 */
public class TestMap {

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int b = is.read(); b >= 0; b = is.read()) baos.write(b);
        return baos.toByteArray();
    }

    public static byte[] getBytes(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            return getBytes(is);
        } finally {
            is.close();
        }
    }


    public static void main(String[] args) {
        try {
            loadMapBlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static  void loadMapBlock() throws IOException {
        byte[][] staticMapBlock = null;
        String configPath =
                "C:\\Users\\jinmiao\\Desktop\\newmapinfo";
        File file = new File(configPath);
        byte[] bytes = getBytes(file);
        ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(bytes.length);
        byteBuf.writeBytes(bytes);
        int weith = byteBuf.readShortLE();
        int height = byteBuf.readShortLE();
        if (staticMapBlock == null) {
            staticMapBlock = new byte[weith][height];
        }

        int maxX=0;
        int maxY=0;
        int maxZ=0;
        int minX=0;
        int minY=0;
        int minZ=0;

        int count = byteBuf.readIntLE();
        for (int i = 0; i < count; i++) {
            int x = byteBuf.readShortLE();
            int y = byteBuf.readShortLE();
            int type = byteBuf.readShortLE();
            int block = byteBuf.readShortLE();
            if(block==1){
                staticMapBlock[x][y] = 1;
            }
            OffsetCoord offsetCoord = new OffsetCoord(x,y);
            Hex hex =offsetCoord.getHex();
            maxX=maxX>hex.x?maxX:hex.x;
            minX=minX<hex.x?minX:hex.x;
            maxY=maxY>hex.y?maxY:hex.y;
            minY=minY<hex.y?minY:hex.y;
            maxZ=maxZ>hex.z?maxZ:hex.z;
            minZ=minZ<hex.z?minZ:hex.z;
        }
        System.out.println("maxX "+maxX+"  minX "+minX+" maxY "+maxY+" minY "+minY+" maxZ "+maxZ+" minZ "+minZ+" ");
        //左上
        //右上
        //左下
        //右下






    }
}
