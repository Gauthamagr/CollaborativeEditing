
//MainDoc - Element which represents the main text area

var mainDoc = document.getElementById("mainDoc");
function setUpPersistantConnection(){
	xmlhttp=new XMLHttpRequest();
	xmlhttp.onreadystatechange=function(){
	
		if(xmlhttp.readyState==4 && xmlhttp.status==200) {
			//document.getElementById("myDiv").innerHTML=xmlhttp.responseText;
		}
	}
	xmlhttp.open("GET","http://localhost:5000",true);
	xmlhttp.send();
} 

function returnKeyFromCode(code){
	 if(code == 8)
	 	var charPressed = "Backspace";
	 else if(code == 32)
	 	var charPressed = "Spacebar";
	 else if(code == 0)
	 	var charPressed = "delete";
	 else 
	 	var charPressed = String.fromCharCode(code);
	return charPressed;
}

function keyPress(e){
	 var textReplace = document.getElementById("textReplace");
	 //textReplace.innerHTML = e.which;
	 textReplace.innerHTML = returnKeyFromCode(e.which)  ;
	 var locationReplace = document.getElementById("location");
	 locationReplace.innerHTML = mainDoc.selectionStart;
}

function setEventHandlers(){
	//alert("Setting up event handlers ");
	mainDoc.onkeypress = function(e){keyPress(e);}
}

function main(){
	setUpPersistantConnection();
	setEventHandlers();
}
main();
