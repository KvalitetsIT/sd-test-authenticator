<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo displayWide=(realm.password && social.providers??); section>
    <#if section = "header">
        ${msg("doLogIn")}
    <#elseif section = "form">
    <div style="background-color: red;" id="kc-form" <#if realm.password && social.providers??>class="${properties.kcContentWrapperClass!}"</#if>>
        <#if realm.password && social.providers??>
            <div id="kc-social-providers" class="${properties.kcFormSocialAccountContentClass!} ${properties.kcFormSocialAccountClass!}">
                <ul class="${properties.kcFormSocialAccountListClass!} <#if social.providers?size gt 4>${properties.kcFormSocialAccountDoubleListClass!}</#if>">
                    <#list social.providers as p>
                        <#if ALLOWED_LOGIN_METHODS?seq_contains(p.alias)>
                            <#if p.alias = "form">
                                <div id="form-placeholder" style="display: none;">${p.loginUrl}</div>

                                <form id="custom-form-login" onsubmit="doFormLogin(); return false;">
                                    <div class="form-group">
                                        <label for="username" class="control-label">Username or email</label>
                                        <input tabindex="1" id="username" class="form-control" name="username" value="" type="text" autofocus="" autocomplete="off">
                                    </div>

                                    <div class="form-group">
                                        <label for="password" class="control-label">Password</label>
                                        <input tabindex="2" id="password" class="form-control" name="password" type="password" autocomplete="off">
                                    </div>

                                    <div id="kc-form-buttons" class="form-group">
                                        <input type="hidden" id="id-hidden-input" name="credentialId">
                                        <input tabindex="4" class="btn btn-primary btn-block btn-lg" name="login" id="kc-login" type="submit" value="Log In">
                                    </div>
                                </form>
                            <#else>
                                <li class="${properties.kcFormSocialAccountListLinkClass!}"><a href="${p.loginUrl}" id="zocial-${p.alias}" class="zocial ${p.providerId}"> <span>${p.displayName}</span></a></li>
                            </#if>
                        </#if>
                    </#list>
                </ul>
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