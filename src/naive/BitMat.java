package naive;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BitMat {
	private BigInteger _mat;
	private int _capacity, _height, _width;
	
	BitMat(int height, int width) {
		_height = height;
		_width = width;
		_mat = BigInteger.ZERO;
		_capacity = computeCapacity(_height, _width);
	}	

	private static int computeCapacity(int height, int width) {
		if(height <= 0) throw new Error("non-positive height");
		if(width <= 0) throw new Error("non-positive width");
		int i=0; 
		for(int h=(height-1); h>0; h>>>=1, i++);
		for(int w=(width-1)>>>i; w>0; w>>>=1, i++);
		return (1<<i);
	}
	
	public void puts() {
		Matcher m = Pattern.compile("[01]{"+_capacity+"}").matcher(_mat.setBit(_capacity*_capacity).toString(2).substring(1));
		int cnt = 0;
		while(m.find() && cnt++<_height) System.out.println(m.group().substring(0, _width));
	}
	
	public void clearBit(int r, int c) {
		if(r < 0 || _height <= r || c < 0 || _width <= c) throw new Error("index out of bounds");
		_mat = _mat.clearBit(_capacity*(_capacity-r)-c-1);
	}
	
	public void setBit(int r, int c) {
		if(r < 0 || _height <= r || c < 0 || _width <= c) throw new Error("index out of bounds");
		_mat = _mat.setBit(_capacity*(_capacity-r)-c-1);
	}
	
	public void assignRow(BigInteger bits, int r) {
		if(bits.bitLength() > _width) throw new Error("too long argument 'bits'");
		int shift = _capacity*(_capacity-r)-_width;
		bits = bits.shiftLeft(shift);
		BigInteger flags = BigInteger.ZERO.setBit(_width).subtract(BigInteger.ONE).shiftLeft(shift);
		_mat = (flags.not().and(_mat)).or(flags.and(bits));
	}
	
	public void assignCol(BigInteger bits, int c) {
		transpose();
		assignRow(bits, c);
		transpose();
	}
	
	private static String rep(String unit, int n) {
		return new String(new char[n]).replace("\0", unit);
	}
	
	public void transpose() {
		int tmp = _width;
		_width = _height;
		_height = tmp;
		
		String zeroRow = rep("0", _capacity);
		for(int i=1; i<_capacity; i<<=1) {
			String ozRow = rep(rep("1", i)+rep("0", i), _capacity/i/2);
			String maskStr = rep(rep(zeroRow, i)+rep(ozRow, i), _capacity/i/2);
			BigInteger mask = new BigInteger(maskStr, 2);
			
			BigInteger diff;
			diff = _mat.shiftRight((_capacity-1)*i).xor(_mat).and(mask);
			diff = diff.or(diff.shiftLeft((_capacity-1)*i));
			_mat = _mat.xor(diff);
		}
	}
	
	public static void main(String[] args) {
		BitMat bm = new BitMat(3, 5);
		bm.puts();
		// bm.setBit(2, 3);
		// bm.puts();
		// bm.clearBit(2, 3);
		// bm.puts();
		bm.assignCol(new BigInteger("111", 2), 1);
		bm.puts();
		bm.transpose();
		bm.puts();
	}
}
