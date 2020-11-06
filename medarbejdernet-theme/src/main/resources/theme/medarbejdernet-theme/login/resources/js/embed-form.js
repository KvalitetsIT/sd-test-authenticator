//$(function() {
//    console.log('Embedding form.');
//
//    // 1. Extract url to initiate login against form-realm
//    console.log('Extracting url.');
//    var formPlaceholder = $('#form-placeholder');
//    if(!formPlaceholder) {
//        // The 'form' login method was not offered in the first place, so we are done.
//        console.log('No url found, returning.');
//        return;
//    }
//
//    var loginUrl = formPlaceholder.text();
//
//    // 2. Invoke the login-url
//    console.log('Making GET-request to ' + loginUrl);
//    $.get(loginUrl, function(data, status) {
//        console.log('data: ' + data);
//        console.log('status: ' + status);
//
//        // Parse the result, extract saml parameters and url
//        var elems = $.parseHTML(data);
//
//        var form = $(elems).find('form[name="saml-post-binding"]');
//        console.log('got form: ' + form);
//
//        // Get action and parameters
//        var action = form.attr('action');
//        var samlRequest = form.children('input[name="SAMLRequest"]').val();
//        var relayState = form.children('input[name="RelayState"]').val();
//
//        console.log('action: ' + action);
//        console.log('samlReuqest' + samlRequest);
//        console.log('relayState: ' + relayState);
//
//        // Perform post request
//        console.log('Making POST-request to ' + action);
//        $.post(action, { SAMLRequest: samlRequest, RelayState: relayState }, function(data, status) {
//            console.log('data: ' + data);
//            console.log('status: ' + status);
//
//            // Extract the login form
//            var formPage = $.parseHTML(data);
//            var loginForm = $(formPage).find('#kc-form-login');
//            console.log('got loginForm: ' + loginForm);
//
//            // Replace the placeholder with the form
//
//            $(formPlaceholder).replaceWith(loginForm);
//        });
//    });
//});

var doFormLogin = function() {
    console.log('Initiating form login ...');

    var username = $('#username').val();
    var password = $('#password').val();

    console.log('Username: ' + username);
    console.log('Password: ' + password);

    // 1. Extract url to initiate login against form-realm
    console.log('Extracting url.');
    var formPlaceholder = $('#form-placeholder');

    var loginUrl = formPlaceholder.text();

    // 2. Invoke the login-url
    console.log('Making GET-request to ' + loginUrl);
    $.get(loginUrl, function(data, status) {
        console.log('data: ' + data);
        console.log('status: ' + status);

        // Parse the result, extract saml parameters and url
        var elems = $.parseHTML(data);

        var form = $(elems).find('form[name="saml-post-binding"]');
        console.log('got form: ' + form);

        // Get action and parameters
        var action = form.attr('action');
        var samlRequest = form.children('input[name="SAMLRequest"]').val();
        var relayState = form.children('input[name="RelayState"]').val();

        console.log('action: ' + action);
        console.log('samlReuqest' + samlRequest);
        console.log('relayState: ' + relayState);

        // Perform post request
        console.log('Making POST-request to ' + action);
        $.post(action, { SAMLRequest: samlRequest, RelayState: relayState }, function(data, status) {
            console.log('data: ' + data);
            console.log('status: ' + status);

            // Extract the login form
            var formPage = $.parseHTML(data);
            var loginForm = $(formPage).find('#kc-form-login');
            console.log('got loginForm: ' + loginForm);

            var loginAction = decodeURI(loginForm.attr('action'));
            console.log('Posting to ' + loginAction);

            var foo = document.createElement('form');
            foo.method = 'post';
            foo.action = loginAction;

            var usernameInput = document.createElement('input');
            usernameInput.type = 'hidden';
            usernameInput.name = 'username';
            usernameInput.value = username;
            foo.appendChild(usernameInput);

            var passwordInput = document.createElement('input');
            passwordInput.type = 'hidden';
            passwordInput.name = 'password';
            passwordInput.value = password;
            foo.appendChild(passwordInput);

            document.body.appendChild(foo);
            foo.submit();


//            // Submit the login form
//            var loginAction = decodeURI(loginForm.attr('action'));
//
//            console.log('Posting to ' + loginAction);
//            $.post({
//                url: loginAction,
//                data: { username: username, password: password },
//                error: function(jqXHR, status, err) {
//                    console.log('err: ' + err);
//                    console.log('status: ' + status);
//                    if(err === 'Internal Server Error') {
//                        window.location.replace('/auth/realms/broker/' + clientId);
//                    }
//                },
//                success: function(data, status) {
//                    console.log('data: ' + data);
//                    console.log('status: ' + status);
//
//                    // Parse the result, extract saml parameters and url
//                    var loginElems = $.parseHTML(data);
//
//                    var form = $(loginElems).find('form[name="saml-post-binding"]');
//                    console.log('got form: ' + form);
//
//                    // Get action and parameters
//                    var action = form.attr('action');
//                    var samlResponse = form.children('input[name="SAMLResponse"]').val();
//                    var relayState = form.children('input[name="RelayState"]').val();
//
//                    console.log('action: ' + action);
//                    console.log('samlResponse: ' + samlResponse);
//                    console.log('relayState: ' + relayState);
//
//                    var foo = document.createElement('form');
//                    foo.method = 'post';
//                    foo.action = action;
//
//                    var srInput = document.createElement('input');
//                    srInput.type = 'hidden';
//                    srInput.name = 'SAMLResponse';
//                    srInput.value = samlResponse;
//                    foo.appendChild(srInput);
//
//                    var rsInput = document.createElement('input');
//                    rsInput.type = 'hidden';
//                    rsInput.name = 'RelayState';
//                    rsInput.value = relayState;
//                    foo.appendChild(rsInput);
//
//                    document.body.appendChild(foo);
//                    foo.submit();
//                }
//            });
        });
    });
};