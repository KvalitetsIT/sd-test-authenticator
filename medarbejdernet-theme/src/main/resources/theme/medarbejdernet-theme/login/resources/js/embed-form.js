$(function() {
    console.log('Embedding form.');

    // 1. Extract url to initiate login against form-realm
    console.log('Extracting url.');
    var formPlaceholder = $('#form-placeholder');
    if(!formPlaceholder) {
        // The 'form' login method was not offered in the first place, so we are done.
        console.log('No url found, returning.');
        return;
    }

    var loginUrl = formPlaceholder.text();

    // 2. Invoke the login-url
    console.log('Calling url ' + loginUrl);
    $.get(loginUrl, function(data) {
        console.log('data: ' + data)

        // Parse the result, extract saml parameters and url
        var elems = $.parseHTML(data);

        var form = $(elems).filter('form[name="saml-post-binding"]');
        console.log('got form: ' + form);

        // Get action and parameters
        var action = form.attr('action');
        var samlRequest = form.children('input[name="SAMLRequest"]').val();
        var relayState = form.children('input[name="RelayState"]').val();

        console.log('action: ' + action);
        console.log('samlReuqest' + samlRequest);
        console.log('relayState: ' + relayState);

        // Perform post request
    });



//            1. Udtræk link fra 'form'-knap
//    	2. get-request med url fra link. Parse resultatet som DOM
//    	3. post-request med værdier fra skridt 2.
//    	4. følge redirect fra skridt 3. Trække formularen ud
//    	5. indsætte formular istedet for 'form'-knap.
});