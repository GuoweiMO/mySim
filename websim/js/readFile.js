/**
 * Created by kwai on 11/08/14.
 */

myObject = {}; //myObject[numberline] = "textEachLine";
$.get('data/AoNA_Flows(for html)', function(file) {
    var lines = file.split("\n");

    for(var i  in lines){
        //each line is "lines[i]"
        //save in object "myObject":
        myObject[i] = lines[i];

        //print in console
        //console.log("line " + i + " :" + myObject[i]);
        return myObject;
    }
}, 'text');

