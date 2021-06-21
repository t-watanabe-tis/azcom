/**
 *
 */

var isSubmitted = false;

function checkSubmit() {


	if(isSubmitted) {

		return false;
	} else {

		isSubmitted = true;
		return true;
	}

}