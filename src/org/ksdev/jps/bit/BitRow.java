package org.ksdev.jps.bit;

/**
 * 32位的 位图一行
 * Created by JinMiao
 * 2021/5/21.
 */
public class BitRow {
    final int BITS_PRE_WORD = 32;
    private int[] bitRows;


    public BitRow(byte[] row,int blockMark) {
        int lenth = row.length / BITS_PRE_WORD;
        lenth+=row.length % BITS_PRE_WORD>0?1:0;
        bitRows = new int[lenth];

        for (int x = 0; x < row.length; x++) {
            boolean block = row[x]==blockMark;
            if(block){
                setBit(x);
            }
        }
    }

    public static void main(String[] args) {
        byte[] row = new byte[]{
                0,0,0,0,0,0,0,1};
        byte[] row1 = new byte[]{
                0,0,0,0,0,1,0,0
        };

        byte[] row2 = new byte[]{
                0,0,1,0,0,0,0,0};
        BitRow bitRow = new BitRow(row,1);
        BitRow bitRow1 = new BitRow(row1,1);
        BitRow bitRow2 = new BitRow(row2,1);

        //_builtin_ffs(((B-<<1) && !B-) ||((B+<<1) && !B+))

        int bit =bitRow.getBit(0);
        int bit2 =bitRow2.getBit(0);

        System.out.println(Integer.toBinaryString(bit));
        System.out.println(Integer.toBinaryString(bit2));
        System.out.println(Integer.toBinaryString((bit<<1) & ~bit));
        System.out.println(Integer.toBinaryString((bit2>>1) & ~bit2));
        //System.out.println(values);

        int values = Integer.numberOfLeadingZeros(((bit>>1) & ~bit) |((bit2>>1) & ~bit2));

        //values = Integer.numberOfTrailingZeros(((bit>>1) & ~bit) |((bit2>>1) & ~bit2));


        System.out.println(Integer.toBinaryString(values));
        System.out.println(values);

        //BitRow bitRow = new BitRow(row,1);
        //System.out.println(bitRow.bitRows.length);
        //System.out.println(Integer.toBinaryString(bitRow.bitRows[0]));
        //System.out.println(Integer.toBinaryString(bitRow.bitRows[0]).length());
        //System.out.println(bitRow);
    }




    private void setBit(int position)
    {
        int values = 1<<(BITS_PRE_WORD-1-(position%BITS_PRE_WORD));
        bitRows[position/BITS_PRE_WORD] |= values;
    }


    /**
     * _builtin_ffs
     * @param position
     * @return
     */
    public int getBit(int position){
        int index = position / BITS_PRE_WORD;
        index+=position % BITS_PRE_WORD>0?1:0;
        return bitRows[index];
    }

    ///**
    // * __builtin_clz
    // */
    //public int __builtin_clz(int position){
    //    int index = position / BITS_PRE_WORD;
    //    index+=position % BITS_PRE_WORD>0?1:0;
    //    int bitRow = bitRows[index];
    //    return Integer.numberOfLeadingZeros(bitRow);
    //}



    @Override
    public String toString() {
        String result = "BitRow{\n";
        for (int i : bitRows) {
            result+=Integer.toBinaryString(i)+"\n";
        }
        result+="}\n";
        return result;
    }
}
