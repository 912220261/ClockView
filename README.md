# ClockView
一个仿照android原生TimePickerDialog控件的时间选择器，以圆形钟表的形式进行显示，转动选取时间，分为小时与分钟两种形式：<br><br>

[](https://github.com/WeicongLi124/ClockView/blob/master/app/src/main/res/drawable/hour.png?raw=true)<br>

[](https://github.com/WeicongLi124/ClockView/blob/master/app/src/main/res/drawable/minutes.png?raw=true)<br>

添加依赖：<br>
```Java
compile 'com.frank.myviewdemo.view.ClockView:clockviewlib:1.0'
```
自定义属性：<br>
```Java
app:timeFormat=""  //钟表样式（小时/分钟）
app:circleTextColor=""  //小圆里字体的颜色
app:selectTextColor=""  //小圆处于的那一圈字体的颜色
app:unSelectTextColor=""  //小圆不处于的那一圈字体的眼神
app:lineColor=""  //连接小圆的线的颜色
app:smallCircleColor=""  //小圆的颜色
app:bigCircleColor=""  //大圆背景颜色
```
以上属性也可以调用控件的相关方法<br>
实现setClockTouchListener()接口,可返回选中的值：
```Java
clockView.setClockTouchListener(new ClockView.ClockTouchListener() {
	@Override
	public void getClockText(String num) {
		textView.setText(num);
	}
});
```
本控件主要通过检测获取触摸屏幕的坐标，判断与某值的坐标范围进行选中，自定义view学了没多久，或多或少有些bug，望见谅。
