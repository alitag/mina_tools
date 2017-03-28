package com.alitag.mina_tools;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * 该类提供了若干验证参数的通用方法，用于简化代码和统一出错信息。
 * </p>
 * <p>
 * 使用示例： 该类通常用于函数中对参数进行检验，如是否为null，是否为空等。使用起来比较简单。
 *
 * <pre>
 * ArgumentValidator.shouldNotBeNull(argument, &quot;argument&quot;);
 * </pre>
 *
 * 如果argument为null，则会抛出一个IllegalArgumentException，其错误信息为&quot;argument should not be null&quot;。
 * </p>
 *
 * <p>
 * 线程安全： 该类线程安全，因为它是一个不可变类，只提供了一些无状态的工具函数。
 * </p>
 *
 * @author gchangyi
 * @version 1.0
 */
public class ArgumentValidator {
	/**
	 * <p>
	 * 默认私有构造函数。防止该类被实例化。
	 * </p>
	 */
	private ArgumentValidator() {
		// do nothing
	}

	/**
	 * <p>
	 * 参数不可为null。如果为null，则抛出IllegalArgumentException，其错误信息为 "argumentName should not be null"
	 * </p>
	 *
	 * @param argument
	 *            需要验证的参数值
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果参数为null
	 */
	public static void notNull(Object argument, String argumentName) {
		if (argument == null)
			throw new IllegalArgumentException(argumentName + " should not be null");
	}

	/**
	 * <p>
	 * 参数不可为""，但可为null。如果为""，则抛出IllegalArgumentException，其错误信息为 "argumentName should not be empty"
	 * </p>
	 *
	 * @param argument
	 *            需要验证的参数值
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果参数为""
	 */
	public static void notEmpty(String argument, String argumentName) {
		if (argument == null)
			return;
		if (argument.length() == 0)
			throw new IllegalArgumentException(argumentName + " should not be empty");
	}

	/**
	 * <p>
	 * 参数不可为null或""。它是notNull()与notEmpty()的叠加。
	 * </p>
	 *
	 * @param argument
	 *            需要验证的参数值
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果参数为null或""
	 */
	public static void notNullOrEmpty(String argument, String argumentName) {
		notNull(argument, argumentName);
		notEmpty(argument, argumentName);
	}

	/**
	 * <p>
	 * 参数不可为""或经过String.trim()之后为""，但可为null。如果为""，则抛出IllegalArgumentException，其错误信息为 "argumentName should not be
	 * empty"。如果为trimmed empty，则抛出IllegalArgumentException，其错误信息为"argumentName should not be trimmed empty"
	 * </p>
	 *
	 * @param argument
	 *            需要验证的参数值
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果参数为""，或者trimmed empty
	 */
	public static void notTrimmedEmpty(String argument, String argumentName) {
		if (argument == null)
			return;
		notEmpty(argument, argumentName);
		if (argument.trim().length() == 0)
			throw new IllegalArgumentException(argumentName + " should not be trimmed empty");
	}

	/**
	 * <p>
	 * 参数不可为null或""或trimmed empty。它是notNull()与notTrimmedEmpty()的叠加。
	 * </p>
	 *
	 * @param argument
	 *            需要验证的参数值
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果参数为null，或者为""，或者trim之后为""
	 */
	public static void notNullOrTrimmedEmpty(String argument, String argumentName) {
		notNull(argument, argumentName);
		notTrimmedEmpty(argument, argumentName);
	}

	/**
	 * <p>
	 * 参数数组不可为空（仅包含0个元素），但是可以为null。如果为空，则抛出IllegalArgumentException异常，其错误信息为"argumentName should not be an empty
	 * array"。另，参数必须是一个数组，否则的话，会抛出IllegalArgumentException，其错误信息为"argumentName should be an array"。
	 * </p>
	 *
	 * @param argumentArray
	 *            需要验证的数组
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果传入的argumentArray不是数组类型，或者为空数组
	 */
	public static void notEmptyArray(Object argumentArray, String argumentName) {
		if (argumentArray == null)
			return;
		checkIsArray(argumentArray, argumentName);
		if (Array.getLength(argumentArray) == 0)
			throw new IllegalArgumentException(argumentName + " should not be an empty array");
	}

	/**
	 * <p>
	 * 参数数组不可为null或空（仅包含0个元素）。它是notNull()与notEmptyArray()的叠加。
	 * </p>
	 *
	 * @param argumentArray
	 *            需要验证的数组
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果传入的argumentArray不是数组类型，或者为null，或者为空数组
	 */
	public static void notNullOrEmptyArray(Object argumentArray, String argumentName) {
		notNull(argumentArray, argumentName);
		notEmptyArray(argumentArray, argumentName);
	}

	/**
	 * <p>
	 * 参数不可为空集合（仅包含0个元素），但是可为null。如果为空，则抛出IllegalArgumentException异常，其错误信息为"argumentName should not be an empty
	 * collection"。
	 * </p>
	 *
	 * @param argument
	 *            需要验证的集合
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果传入的集合为空
	 */
	public static void notEmptyCollection(Collection<?> argument, String argumentName) {
		if (argument == null)
			return;
		if (argument.isEmpty())
			throw new IllegalArgumentException(argumentName + " should not be an empty collection");
	}

	/**
	 * <p>
	 * 参数集合不可为null或空（仅包含0个元素）。它是notNull()和notEmptyCollection()的叠加。
	 * </p>
	 *
	 * @param argument
	 *            需要验证的集合
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果参数为null，或者为空集合
	 */
	public static void notNullOrEmptyCollection(Collection<?> argument, String argumentName) {
		notNull(argument, argumentName);
		notEmptyCollection(argument, argumentName);
	}

	/**
	 * <p>
	 * 参数数组不可包含null元素，但是它可以为null或空。如果包含null元素，则抛出IllegalArgumentException，其错误信息为"argumentName should not contains
	 * null-value element(s)"。
	 * </p>
	 *
	 * @param argumentArray
	 *            需要验证的数组
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果传入的参数包含了null值
	 */
	public static void arrayNotContainsNull(Object[] argumentArray, String argumentName) {
		if (argumentArray == null)
			return;
		for (Object object : argumentArray) {
			if (object == null)
				throw new IllegalArgumentException(argumentName + " should not contains null-value element(s)");
		}
	}

	/**
	 * <p>
	 * 参数数组不可包含null元素或空元素，但是它可以为null或空。<br>
	 * 如果包含null元素，则抛出IllegalArgumentException，其错误信息为"argumentName should not contains null-value element(s)"。<br>
	 * 如果包含空元素，则抛出IllegalArgumentException，其错误信息为"argumentName should not contains empty element(s)"。<br>
	 * </p>
	 *
	 * @param argumentArray
	 *            需要验证的数组
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果传入的参数包含了null值或者空值
	 */
	public static void arrayNotContainsNullOrEmpty(String[] argumentArray, String argumentName) {
		if (argumentArray == null)
			return;
		arrayNotContainsNull(argumentArray, argumentName);
		for (String argument : argumentArray) {
			if (argument.length() == 0)
				throw new IllegalArgumentException(argumentName + " should not contains empty element(s)");
		}
	}

	/**
	 * <p>
	 * 参数数组不可包含null元素或空元素，但是它可以为null或空。<br>
	 * 如果包含null元素，则抛出IllegalArgumentException，其错误信息为"argumentName should not contains null-value element(s)"。<br>
	 * 如果包含空元素，则抛出IllegalArgumentException，其错误信息为"argumentName should not contains empty element(s)"。<br>
	 * 如果包含trimmed empty，则抛出IllegalArgumentException，其错误信息为"argumentName should not contains trimmed empty element(s)"。
	 * </p>
	 *
	 * @param argumentArray
	 *            需要验证的数组
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果传入的参数包含了null值或者trim之后为空值
	 */
	public static void arrayNotContainsNullOrTrimmedEmpty(String[] argumentArray, String argumentName) {
		if (argumentArray == null)
			return;
		arrayNotContainsNullOrEmpty(argumentArray, argumentName);
		for (String argument : argumentArray) {
			if (argument.trim().length() == 0)
				throw new IllegalArgumentException(argumentName + " should not contains trimmed empty element(s)");
		}
	}

	/**
	 * <p>
	 * 参数集合不可包含null元素，但是它可以为null或空。如果包含null元素，则抛出IllegalArgumentException，其错误信息为"argumentName should not contains
	 * null-value element(s)"
	 * </p>
	 * <p>
	 * 注意：不可以直接使用<code>Collection#contains(null)</code>来判断，因为有一些Collection的实现类，在传入的参数为null时，会抛出NullPointerException。
	 * </p>
	 *
	 * @param argument
	 *            需要验证的集合
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果传入的集合包含了null值
	 */
	public static void collectionNotContainsNull(Collection<?> argument, String argumentName) {
		if (argument == null || argument.isEmpty())
			return;

		for (Object object : argument) {
			if (object == null)
				throw new IllegalArgumentException(argumentName + " should not contains null-value element(s)");
		}
	}

	/**
	 * <p>
	 * 参数Map不可以包含为null的key，但参数本身可为null。如果包含为null的key，则抛出IllegalArgumentException，其错误信息为"argumentName should not contains
	 * null-value key(s)"。
	 * </p>
	 * <p>
	 * 注：不可以直接调用map.containsKey(null)，该方法根据实现的不同，可能会抛出NullPointerException，参见：Map#containsKey(Object)的文档。
	 * </p>
	 *
	 * @param argument
	 *            需要验证的Map
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果参数map包含了为null的key
	 */
	public static void notContainsNullKey(Map<?, ?> argument, String argumentName) {
		if (argument == null || argument.isEmpty())
			return;

		for (Object key : argument.keySet()) {
			if (key == null)
				throw new IllegalArgumentException(argumentName + " should not contains null-value key(s)");
		}
	}

	/**
	 * <p>
	 * 参数Map不可以包含为null的value，但参数本身可为null。如果包含为null的value，则抛出IllegalArgumentException，其错误信息为"argumentName should not
	 * contains null-value value(s)"。
	 * </p>
	 * <p>
	 * 注：不可以直接调用map.containsValue(null)，该方法根据实现的不同，可能会抛出NullPointerException，参见：Map#containsValue(Object)的文档。
	 * </p>
	 *
	 * @param argument
	 *            需要验证的Map
	 * @param argumentName
	 *            参数名称，用于生成错误信息
	 * @throws IllegalArgumentException
	 *             如果参数map包含了为null的value
	 */
	public static void notContainsNullValue(Map<?, ?> argument, String argumentName) {
		if (argument == null || argument.isEmpty())
			return;
		for (Object object : argument.values()) {
			if (object == null)
				throw new IllegalArgumentException(argumentName + " should not contains null-value value(s)");
		}
	}

	/**
	 * <p>
	 * 条件应该为true。否则会抛出IllegalArgumentException,其错误信息为传入的message。
	 * </p>
	 *
	 * @param message
	 *            完整的错误信息
	 * @param condition
	 *            需要验证的条件
	 * @throws IllegalArgumentException
	 *             如果条件为false
	 */
	public static void isTrue(boolean condition, String message) {
		if (!condition)
			throw new IllegalArgumentException(message);
	}

	/**
	 * <p>
	 * Check if a object is an array actually.
	 * </p>
	 *
	 * @param argumentArray
	 *            to be checked
	 * @param argumentName
	 *            argument name
	 * @throws IllegalArgumentException
	 *             if the object is not an array
	 */
	private static void checkIsArray(Object argumentArray, String argumentName) {
		if (!argumentArray.getClass().isArray())
			throw new IllegalArgumentException(argumentName + " should be an array");
	}
}
