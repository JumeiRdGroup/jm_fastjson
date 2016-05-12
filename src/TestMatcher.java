import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * Copyright (C) 2010-2015 Jumei Inc.All Rights Reserved.
 * FileName：TestMatcher.java
 * Description：
 * History：
 * 1.0 cdyf 2015-11-13 Create
 */

public class TestMatcher
{
	public static void main(String[] args)
	{
		String s = "aaaaa#{xxx}bbbb#{yyy}cccccc#{zzz}";
		Matcher m = Pattern.compile("#\\{.*?\\}").matcher(s);
		while (m.find())
		{
			System.err.println(m.group());
		}

		List<MatchResult> test2 = test2("", "");
		System.out.println(test2.toString());
		
		 test1();
	}


	public static class MatchResult
	{
		public int	mStart	= 0;

		public int	mCount	= 0;

		public MatchResult()
		{

		}

		public MatchResult(int start, int count)
		{
			mStart = start;
			mCount = count;
		}
		
		@Override
		public String toString()
		{
			return "MatchResult [mStart=" + mStart + ", mCount=" + mCount + "]";
		}
	}

	private static List<MatchResult> test2(String target, String match)
	{

		List<MatchResult> result = new LinkedList<MatchResult>();

		if (target == null || match == null)
			return result;

		target = "火车北站公交站";
		match = "火车站";
		boolean[] targetFlag = new boolean[target.length()];
		char[] charArray = match.toCharArray();
		for (char ch : charArray)
		{
			int indexOf = target.indexOf(ch);
			if (indexOf != -1)
			{
				targetFlag[indexOf] = true;
			}
		}
		System.out.println(Arrays.toString(targetFlag));

		for (int i = 0; i < targetFlag.length; i++)
		{
			if (!targetFlag[i])
			{
				continue;
			}
			MatchResult matchItem = new MatchResult(i, 1);
			result.add(matchItem);
			for (int j = i + 1; j < targetFlag.length && targetFlag[j]; j++)
			{
				matchItem.mCount++;
				i = j;
			}
		}

		return result;
	}

	private static void test1()
	{
		String country = "火车北站公交站";
		Pattern mpattern = Pattern.compile("(火.*?)|(车.*?)|(站.*?)");
		Matcher mmatcher = mpattern.matcher(country);
		String str = "";
		java.util.regex.MatchResult matchResult = mmatcher.toMatchResult();
		while (mmatcher.find())
		{
			str = mmatcher.group();
			System.out.println(str);
		}
	}
}
