<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <#if section = "header">
    <#elseif section = "form">

    <div id="kc-form" <#if realm.password && social.providers??>class="${properties.kcContentWrapperClass!}"</#if>>
        <#if realm.password && social.providers??>
            <div id="kc-social-providers">

					<ul class="nav nav-tabs">
	                    <#list social.providers as p>
	                        <#if ALLOWED_LOGIN_METHODS?seq_contains(p.alias)>
	                          <li class="nav-item">
	                          		<#if SELECTED_LOGIN?seq_contains(p.alias)>
		                          	    <a class="nav-link active" id="a-${p.alias}" href="#" onclick="function setLoginMethod(alias) { selectLoginMethodWithChannel(alias, 'medarbejdernet'); }; setLoginMethod('${p.alias}'); return false;" id="zocial-${p.alias}" class="zocial ${p.providerId}"><span>${p.displayName}</span></a>
		                          	<#else>
		                          	    <a class="nav-link" id="a-${p.alias}" href="#" onclick="function setLoginMethod(alias) { selectLoginMethodWithChannel(alias, 'medarbejdernet'); }; setLoginMethod('${p.alias}'); return false;" id="zocial-${p.alias}" class="zocial ${p.providerId}"><span>${p.displayName}</span></a>
		                          	</#if>
					  		  </li>
	                        </#if>
	                    </#list>
					</ul>

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

