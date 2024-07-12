# 关于security的设计


## 类图设计


```text
// {type:class}
// {direction:topDown}
// {generate:true}
[note: You can stick notes on diagrams too!{bg:cornsilk}]

[Customer]<>0-orders 0..*>[Order]
[Order]++*-*>[LineItem]
[Order]-2>[DeliveryMethod]
[Order]*-*>[Product|EAN_Code;Description;ListPrice|promo_price()]
[Category]<->[Product]
[DeliveryMethod]^[National]
[DeliveryMethod]^[International]
```




## Mu Server中的类关系图

```yuml
// {type: class}
// {direction: leftToRight}
// {generate: true}

[<<HttpMessage>>||protocolVersion();setProtocolVersion();headers()]^-.-[<<HttpRequest>>||method();uri();setUri();]
[<<HttpMessage>>]^-.-[<<HttpResponse>>||status();setStatus();]

[NettyRequestAdapter|contextPath]^[MuRequest]

[NettyRequestAdapter|contextPath]->[HttpRequest]
[NettyRequestAdapter|contextPath]->[RequestState]
[NettyRequestAdapter|contextPath]->[Method]
[NettyRequestAdapter|contextPath]->[Headers]
[NettyRequestAdapter|contextPath]->[RequestBodyReader]
[NettyRequestAdapter|contextPath]->[RequestParameters]
[NettyRequestAdapter|contextPath]->[List<Cookie>]


```