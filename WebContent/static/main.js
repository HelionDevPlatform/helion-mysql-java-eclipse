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
	

	
		
	
	loadTable();
	//setInterval(function(){ loadTable()},2000);
	
	console.log("ready")
});



