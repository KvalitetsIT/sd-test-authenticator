<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "title">
        ${msg("loginTitle",realm.name)}
    <#elseif section = "header">
    <#elseif section = "form">
        <form id="kc-totp-login-form" action="${url.loginAction}" method="post">
        </form>
        
        <script type="text/javascript">
        	console.log('Running SD finalize login!');
        	if(this != top){
	        	console.log('Setting myself as top');
  				top.document.location.href = this.document.location.href;
			} else {		
		        console.log('Submitting finalizeform');
				document.getElementById("kc-totp-login-form").submit();
			}
		</script>
    </#if>
</@layout.registrationLayout>