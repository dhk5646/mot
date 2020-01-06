/**
 * 
 */

$( document ).ready( function() {
	
	fnInitEvent();
	
	
	function fnInitEvent() {
		$("#buttonCalculator").on('click', function () { fnCalculator(); });
	}
	
	function fnCalculator() {
		
		var param1 = $("#param1").val();
		var expression = $("#expression").val();
		var param2 = $("#param2").val();
		
		return;
		$.ajax({  
			url:'/smp/sample/calculator.mvc',
			data : "test",
			async : true,
            success: function(data) {
                 $('#area').html(data);
            }
        });
		
	}
} );


//alert("안녕하세요");


function fnCalculator1() {
	//alert("안녕2");
}

var jsonStr = new Object();


var param1 = [ 'Lorem', 'Ipsum', 'Dolor' ];
var param2 = [ 'Lorem', 'Ipsum', 'Dolor' ];
var param3 = [ 'Lorem', 'Ipsum', 'Dolor' ];


jsonStr.header = param1;
jsonStr.body = param2;
jsonStr.paging = param3;

console.log(JSON.stringify(jsonStr));

//{inputData":"{"header":{"Lorem":"test","Ipsum":"test","Dolor":"test"},"body":["Lorem","Ipsum","Dolor"],"paging":["Lorem","Ipsum","Dolor"]}"}
