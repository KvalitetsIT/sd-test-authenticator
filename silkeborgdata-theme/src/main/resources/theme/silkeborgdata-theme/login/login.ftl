<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo displayWide=(realm.password && social.providers??); section>
    <#if section = "header">
        ${msg("doLogIn")}
    <#elseif section = "form">

    <div style="background-color: red;" id="kc-form" <#if realm.password && social.providers??>class="${properties.kcContentWrapperClass!}"</#if>>
        <#if realm.password && social.providers??>
            <div id="kc-social-providers" class="${properties.kcFormSocialAccountContentClass!} ${properties.kcFormSocialAccountClass!}">

                    <#list social.providers as p>
                        <#if ALLOWED_LOGIN_METHODS?seq_contains(p.alias)>
                          	<a id="a-${p.alias}" href="${p.loginUrl}" onclick="function setLoginMethod(alias) { console.log('hej1 !'); selectLoginMethod(alias); }; setLoginMethod('${p.alias}'); return false;" id="zocial-${p.alias}" class="zocial ${p.providerId}"><span>${p.displayName}</span></a> 
                        </#if>
                    </#list>
                    
                    <#list social.providers as p>
                        <#if ALLOWED_LOGIN_METHODS?seq_contains(p.alias)>
		                    <#if SELECTED_LOGIN?seq_contains(p.alias)>
		                    	<div class="sdcontainer">
 				           		<iframe id="sdloginiframe" class="sdresponsive-iframe" src="${p.loginUrl}" />
 				           		</div>
                		    </#if>
                        </#if>
                    </#list>
            </div>
        </#if>
      </div>
    <#elseif section = "info" >
        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
            <div id="kc-registration">
                <span>${msg("noAccount")} <a tabindex="6" href="${url.registrationUrl}">${msg("doRegister")}</a></span>
            </div>
        </#if>
    </#if>

</@layout.registrationLayout>