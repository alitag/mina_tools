package com.alitag.mina_tools;

import java.nio.charset.Charset;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineDecoder;
import org.apache.mina.filter.codec.textline.TextLineEncoder;

/**
 * <p>
 * 该类是一个基于文本行的编解码器的工厂类。它可以指定编码器及解码器的字符集，使用的换行符，每行的最大长度等参数。
 * </p>
 * 
 * @author gchangyi
 * @version 1.0
 */
public class TextLineCodecFactory implements ProtocolCodecFactory {

	/**
	 * 持有的解码器对象，初始值为null，将在构造函数中被初始化。
	 */
	private TextLineDecoder decoder;
	/**
	 * 持有的编码器对象，初始值为null，将在构造函数中被初始化。
	 */
	private TextLineEncoder encoder;

	/**
	 * decoder在解析收到的文本时，使用的换行符。默认值是{@link LineDelimiter#AUTO}，表示自动判断
	 */
	private LineDelimiter decoderLineDelimiter = LineDelimiter.AUTO;

	/**
	 * decoder在解析收到的文本时，使用的字符集。默认值为iso-8859-1
	 */
	private String decoderCharset = "iso-8859-1";

	/**
	 * encoder在发送文本时，使用的换行符。默认值是{@link LineDelimiter#WINDOWS}
	 */
	private LineDelimiter encoderLineDelimiter = LineDelimiter.WINDOWS;

	/**
	 * encoder在发送文本时，使用的字符集。默认值是iso-8859-1
	 */
	private String encoderCharset = "iso-8859-1";

	/**
	 * decoder在解析收到的文本时，可接受的每行最大长度。默认值是10240
	 */
	private int decoderLineLength = 10240;

	/**
	 * encoder在发送文本时，可接受的每行最大长度。默认值是102400
	 */
	private int encoderLineLength = 102400;

	/**
	 * <p>
	 * 默认构造函数。使用以下默认参数来构造：
	 * <ul>
	 * decoder:
	 * <li>lineDelimiter:auto(自动识别)
	 * <li>encoding:iso-8859-1
	 * <li>lineSize:10240
	 * </ul>
	 * <ul>
	 * encoder:
	 * <li>lineDelimiter:windows
	 * <li>encoding:iso-8859-1
	 * <li>lineSize:102400
	 * </ul>
	 * </p>
	 * 
	 */
	public TextLineCodecFactory() {
		init();
	}

	/**
	 * <p>
	 * 构造函数，可以指定decoder和encoder的各属性。
	 * </p>
	 * <p>
	 * 注意：decoderLineDelimiter和encoderLineDelimiter不能为DEFAULT，是因为Mina中的DEFAULT有bug，它无法得到当前系统的换行符。
	 * </p>
	 * 
	 * @param decoderCharset
	 *            decoder使用的字符集
	 * @param decoderLineDelimiter
	 *            decoder用于解析收到的文本的换行符。不可为null。如果为auto，则表示自动判断
	 * @param encoderCharset
	 *            encoder使用的字符集
	 * @param encoderLineDelimiter
	 *            encoder用于解析收到的文本的换行符。不可为null。不可为auto，必须显式指定
	 * @throws IllegalArgumentException
	 *             如果任一参数为null，或者字符集为trimmed empty或不存在，或者decoderLineDelimiter为DEFAULT,
	 *             或者encoderLineDelimiter为LineDelimiter.AUTO或LineDelimiter.DEFAULT,
	 *             或者encoderCharset与encoderCharset不被系统支持。
	 */
	public TextLineCodecFactory(String decoderCharset, LineDelimiter decoderLineDelimiter, String encoderCharset,
			LineDelimiter encoderLineDelimiter) {
		ArgumentValidator.notNull(decoderLineDelimiter, "decoderLineDelimiter");
		ArgumentValidator.notNullOrTrimmedEmpty(decoderCharset, "decoderCharset");
		ArgumentValidator.notNull(encoderLineDelimiter, "encoderLineDelimiter");
		ArgumentValidator.notNullOrTrimmedEmpty(encoderCharset, "encoderCharset");
		ArgumentValidator.isTrue(decoderLineDelimiter != LineDelimiter.DEFAULT,
				"decoderLineDelimiter should not be DEFAULT");
		ArgumentValidator.isTrue(encoderLineDelimiter != LineDelimiter.AUTO, "encoderLineDelimiter should not be AUTO");
		ArgumentValidator.isTrue(encoderLineDelimiter != LineDelimiter.DEFAULT,
				"encoderLineDelimiter should not be DEFAULT");
		ArgumentValidator.isTrue(Charset.isSupported(decoderCharset), "decoderCharset is not supported: "
				+ decoderCharset);
		ArgumentValidator.isTrue(Charset.isSupported(encoderCharset), "encoderCharset is not supported: "
				+ encoderCharset);

		this.decoderLineDelimiter = decoderLineDelimiter;
		this.decoderCharset = decoderCharset;
		this.encoderLineDelimiter = encoderLineDelimiter;
		this.encoderCharset = encoderCharset;

		init();
	}

	/**
	 * 使用指定的参数初始化decoder和encoder
	 */
	private void init() {
		decoder = new TextLineDecoder(Charset.forName(decoderCharset), decoderLineDelimiter);
		encoder = new TextLineEncoder(Charset.forName(encoderCharset), encoderLineDelimiter);
		setMaxLineLength(decoderLineLength, encoderLineLength);
	}

	/**
	 * <p>
	 * 设置编解码器接受的每行最大长度。必须为正数。
	 * </p>
	 * 
	 * @param decoderLineLength
	 *            decoder的每行最大长度，必须为正数
	 * @param encoderLineLength
	 *            encoder的每行最大长度，必须为正数
	 * @throws IllegalArgumentException
	 *             如果decoderSize或者encoderSize小于等于0
	 */
	public TextLineCodecFactory setMaxLineLength(int decoderLineLength, int encoderLineLength) {
		ArgumentValidator.isTrue(decoderLineLength > 0, "decoderLineLength should >0: " + decoderLineLength);
		ArgumentValidator.isTrue(encoderLineLength > 0, "encoderLineLength should >0: " + encoderLineLength);

		this.decoderLineLength = decoderLineLength;
		this.encoderLineLength = encoderLineLength;
		decoder.setMaxLineLength(this.decoderLineLength);
		encoder.setMaxLineLength(this.encoderLineLength);
		return this;
	}

	/**
	 * <p>
	 * 得到解码器对象
	 * </p>
	 * 
	 * @return 解码器对象
	 */
	public TextLineDecoder getDecoder() {
		return decoder;
	}

	/**
	 * <p>
	 * 得到编码器对象
	 * </p>
	 * 
	 * @return 编码器对象
	 */
	public TextLineEncoder getEncoder() {
		return encoder;
	}

	@Override
	public String toString() {
		return "decoder: " + decoderCharset + ", " + getDelimiterName(decoderLineDelimiter) + ", " + decoderLineLength
				+ "; encoder: " + encoderCharset + ", " + getDelimiterName(encoderLineDelimiter) + ", "
				+ encoderLineLength;
	}

	/**
	 * 由LineDelimiter得到对应的名称
	 */
	private String getDelimiterName(LineDelimiter lineDelimiter) {
		if (lineDelimiter == LineDelimiter.WINDOWS)
			return "WINDOWS";
		else if (lineDelimiter == LineDelimiter.MAC)
			return "MAC";
		else if (lineDelimiter == LineDelimiter.UNIX)
			return "UNIX";
		else if (lineDelimiter == LineDelimiter.AUTO)
			return "AUTO";
		return "DEFAULT";
	}
}
