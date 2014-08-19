			// Load the Visualization API and the Linechart package.
	    google.load('visualization', '1', {'packages':['corechart']});
	    	      
	    // Set a callback to run when the Google Visualization API is loaded.
	    google.setOnLoadCallback(drawChart_ft);
	      
	    function drawChart_ft() {
	      var jsonData = $.ajax({
	          url: "src/getFixedToll.php",
	          dataType:"json",
	          async: false
	          }).responseText;
	      
	      var options = {
            title: 'Fixed Toll Pricing (75 iterations)',
    		curveType: 'function',
            legend:{position: 'bottom'},
            tooltip:{trigger: 'focus'},
    		explorer: {axis:'true'}
  		  };
	      // Create our data table out of JSON data loaded from server.
	      var data = new google.visualization.DataTable(jsonData);
	
	      // Instantiate and draw our chart, passing in some options.
	      var chart = new google.visualization.LineChart(document.getElementById('display7'));
	      chart.draw(data, options);
	    }

		