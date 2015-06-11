// main.js


        
$(document).ready(function() {
	
	var t = $('#content').DataTable( {
        "order": [[ 0, "desc" ]]
    });
	

	function loadTable() {
		$.get('allData',function(data, status) {
			
			console.log(data);
			var dataLen = data.length;
			t.rows().remove();
			for( var i = 0; i < dataLen; i++) {
				o = data[i];
				console.log(o.id);
				console.log(o.value);
				t.row.add([o.id,o.value]).draw();
			}
			
						
		});
	}
	
//	$('#submit_button').click(function(e) {
//		var url = "/fib";
//		
//		$.ajax({
//		  type: "POST",
//		  url: url,
//		  data: JSON.stringify({"number": $("#gen_fib").val()}),
//		  contentType: "application/json",
//          dataType: "json",
//		  success: function(data){
//		  	console.log(data);
//		  	$("#result").html("<span>The number you sent was: "+$("#gen_fib").val()+ ". The returned value was: "+data.fib_value+"</span>");
//		  	
//		  }
//		});
//
//		
//	});
	
		
	
	loadTable();
	//setInterval(function(){ loadTable()},2000);
	
	console.log("ready")
});



