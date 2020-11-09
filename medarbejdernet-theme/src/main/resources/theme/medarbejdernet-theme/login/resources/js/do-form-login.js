var doFormLogin = function() {
    console.log('Initiating form login ...');

    // 1. Extract url to initiate login against form-realm
    console.log('Extracting url.');
    var formPlaceholder = $('#form-placeholder');

    var loginUrl = formPlaceholder.text();

    // 2. Start the login-flow
    console.log('Making GET-request to ' + loginUrl);
    $.get(loginUrl, sendSamlRequestHandler);
};

var sendSamlRequestHandler = function(data, status) {
    console.log('data: ' + data);
    console.log('status: ' + status);

    // Parse the result, extract saml parameters and url
    var parsedResult = $.parseHTML(data);

    var form = $(parsedResult).find('form[name="saml-post-binding"]');
    console.log('got form: ' + form);

    // Get action and parameters
    var action = form.attr('action');
    var samlRequest = form.children('input[name="SAMLRequest"]').val();
    var relayState = form.children('input[name="RelayState"]').val();

    console.log('action: ' + action);
    console.log('samlRequest' + samlRequest);
    console.log('relayState: ' + relayState);

    // Perform post request
    console.log('Making POST-request to ' + action);
    $.post(action, { SAMLRequest: samlRequest, RelayState: relayState }, sendCredentialsHandler);
};

var sendCredentialsHandler = function(data, status) {
    console.log('data: ' + data);
    console.log('status: ' + status);

    var username = getUsernameFromDocument();
    var password = getPasswordFromDocument();

    console.log('Username: ' + username);
    console.log('Password: ' + password);

    // Extract the login form
    var formPage = $.parseHTML(data);
    var loginForm = $(formPage).find('#kc-form-login');
    console.log('got loginForm: ' + loginForm);

    // Get the action
    var loginAction = decodeURI(loginForm.attr('action'));
    console.log('Posting to ' + loginAction);

    // Create a form and submit it
    var loginForm = createLoginForm(document, loginAction, username, password);
    loginForm.submit();
};

var getUsernameFromDocument = function() {
    return $('#username').val();
};

var getPasswordFromDocument = function() {
    return $('#password').val();
};

var createLoginForm = function(document, action, username, password) {
    var loginForm = document.createElement('form');
    loginForm.method = 'post';
    loginForm.action = action;

    addFormInput(document, loginForm, 'username', username);
    addFormInput(document, loginForm, 'password', password);

    // Add the form to the document to ensure that submitting it actually does something
    document.body.appendChild(loginForm);

    return loginForm;
};

var addFormInput = function(document, form, inputName, inputValue) {
    var input = document.createElement('input');
    input.type = 'hidden';
    input.name = inputName;
    input.value = inputValue;
    form.appendChild(input);
};