# jm_fastjson
### 聚美优品fastjson(ali)的定制化开发
聚美优品根据fastjson的安卓版本库源码做的定制修改,目前新加的功能点有三个:
* 支持聚美图片字典解析,通过`@JMIMG`标注一个类型为String的字段是否为聚美图片字段,如果是聚美图片字段,本库根据手机分辨率自动选择最佳图片url.
* 在解析场景为字段增加多alias支持,避免仅仅是因为字段名不同而反复生成Rsp协议文件.
* 针对聚美后台采用php下发json导致对象实体为空时使用中括号代替大括号的bug,修改了解析逻辑,原来逻辑是抛异常,修改为不抛出异常且放弃解析该字段.

用法参考：RogerRsp.java和TestDriver.java.




```java

public class RogerRsp
{

  public int  			mInt ;
  
  //聚美图片注解自动解析。
  @JMIMG
  public String				imgUrl;

  //多别名支持
  @JSONField( alias= {"data", "kabc"})
  public Data		mData;

  public ArrayList<String> name;

  public static class Data
  {
      private  String mValue;

      @JMIMG
      @JSONField(name="good_value", alias= {"goodvalue", "kabc"})
      public void setValue(String v)
      {
          mValue = v;
      }

      public String getValue()
      {
          return mValue;
      }
  }
}

```

兼容中括号的用法如下：



```java
public enum Feature
{
 .................

  /**
   * 允许为空的对象在没有值时使用中括号代替大括号.
   */
  IgnoreEmptyBracket;
..................
}


//
......................
public static final String	testCase	= "{\"imgUrl\":" + imgTest + " ,\"denver\":21, \"Data\":{\"good_value\":" + url + "}  ,\"name\":[\"namea\",\"nameb\",\"namec\",\"named\",\"namee\"] 			 }";
RogerRsp p = JSON.parseObject(testCase, RogerRsp.class, Feature.IgnoreEmptyBracket);
```
