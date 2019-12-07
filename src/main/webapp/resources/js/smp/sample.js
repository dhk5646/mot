/**
 * 
 */

alert("안녕하세요");




var jsonStr = new Object();


var param1 = [ 'Lorem', 'Ipsum', 'Dolor' ];
var param2 = [ 'Lorem', 'Ipsum', 'Dolor' ];
var param3 = [ 'Lorem', 'Ipsum', 'Dolor' ];


jsonStr.header = param1;
jsonStr.body = param2;
jsonStr.paging = param3;

console.log(JSON.stringify(jsonStr));

//{inputData":"{"header":{"Lorem":"test","Ipsum":"test","Dolor":"test"},"body":["Lorem","Ipsum","Dolor"],"paging":["Lorem","Ipsum","Dolor"]}"}
