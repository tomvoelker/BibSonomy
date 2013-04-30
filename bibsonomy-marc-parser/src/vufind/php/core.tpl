<div class="span-12">
  
  {* Display Title *}
  <h1>{$coreShortTitle|escape}
  </h1>  
  {if $coreSubtitle}<p>{$coreSubtitle|escape}</p>{/if}
  <!--  {if !empty($coreTitleSection)}
     {foreach key=schluessel item=wert from=$coreTitleSection}
       <p>{$wert|escape}</p>
     {/foreach}  
  {/if} -->
  {* End Title *}
 
  {if $coreSummary}<p>{$coreSummary|truncate:300:"..."|escape} <a href='{$url}/Record/{$id|escape:"url"}/Description#tabnav'>{translate text='Full description'}</a></p>{/if}

  {* Display Main Details *}
  <table cellpadding="2" cellspacing="0" border="0" class="citation" summary="{translate text='Bibliographic Details'}">
    
    {* Teil bzw. Unterreihe *}
    {if $coreTitleSection.0.n || $coreTitleSection.0.p}
    <tr valign="top" class="coreTitleSection" >
      <th>{translate text=$coreTitleSectionType}: </th>
      <td>
        {foreach key=schluessel item=wert from=$coreTitleSection name=loop}
            {$wert.n|escape}{if !empty($wert.p) && !empty($wert.n)}. {/if}{$wert.p|escape}
            {if !$smarty.foreach.loop.last}
               <br>
            {/if}   
        {/foreach}
      </td>
    </tr>
    {/if}
    
    {* darin enthalten *}
    {if !empty($coreContained.0) || !empty($coreContained.1)}
    <tr valign="top" class="coreContained" >
    <th>{translate text="darin enthalten"}: </th>
    <td>{$coreContained.0}{if !empty($coreContained.0)}<br>{/if}{$coreContained.1}</td>
    </tr>
    {/if}
    
    {* Autor *}
    {if !empty($coreMainAuthor) || !empty($coreSecondaryAuthor)}
    <tr valign="top" class="coreMainAuthor">
      <th>{translate text='Author'}: </th>
      <td>
      {if !empty($coreMainAuthor)}
         <a href="{$url}/Author/Home?author={$coreMainAuthor|escape:"url"}">{$coreMainAuthor|escape}</a>
      {/if}
      {foreach from=$coreSecondaryAuthor item=wert name=loop}
           ; <a href="{$url}/Author/Home?author={$wert|escape:"url"}">{$wert|escape}</a> 
      {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Beteiligt *}
    {if !empty($corePartAuthor)}
    <tr valign="top" class="corePartAuthor" >
      <th>{translate text='Beteiligt'}: </th>
      <td>
      {foreach from=$corePartAuthor item=wert name=loop}
        <a href="{$url}/Author/Home?author={$wert.a|escape:"url"}">{$wert.a|escape}</a>{if $wert.e} ({$wert.e|escape}){/if}{if !$smarty.foreach.loop.last}; {/if} 
      {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Koerperschaft *}
    {if !empty($coreCorporation)}
    <tr valign="top" class="coreCorporation" >
      <th>{translate text='Körperschaft'}: </th>
      <td>
      {foreach from=$coreCorporation item=wert name=loop}
        {$wert|escape:"html"}{if !$smarty.foreach.loop.last}<br>{/if} 
      {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Festschrift *}
    {if !empty($coreFestschrift)}
    <tr valign="top" class="coreFestschrift" >
      <th>{translate text='Festschrift für'}: </th>
      <td>
      {foreach from=$coreFestschrift item=wert name=loop}
        {$wert|escape:"html"}{if !$smarty.foreach.loop.last}; {/if} 
      {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Interpret *}
    {if !empty($coreInterpreter.0) || !empty($coreInterpreter.1)}
    <tr valign="top" class="coreInterpreter" >
      <th>{translate text='Interpret'}: </th>
      <td>
      {foreach from=$coreInterpreter.0 item=wert name=loop}
        {$wert|escape:"html"}{if !$smarty.foreach.loop.last}; {/if} 
      {/foreach}
      {foreach from=$coreInterpreter.1 item=wert name=loop}
        <br>{$wert|escape:"html"}
      {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Display Journal *}
    {if !empty($coreJournal)}
    <tr valign="top" class="coreJournal">
      <th>{$coreJournal.prefix}: </th>
      <td>
        {if !empty($coreJournal.ppn)}
        <a href="{$url}/Search/Results?lookfor=HEB{$coreJournal.ppn|escape:"url"}&amp;type=id">{$coreJournal.name|escape}</a>
        {else}
        {$coreJournal.name|escape}
        {/if}
        {if !empty($coreJournal.band)}{$coreJournal.band | escape:"html"}{/if}
        {if !empty($coreJournal.jahr)} ({$coreJournal.jahr | escape:"html"}){/if}{if !empty($coreJournal.kommentar)}, {$coreJournal.kommentar | escape:"html"}{/if}{if !empty($coreJournal.seite)}, S. {$coreJournal.seite | escape:"html"}{/if}</br> 
        {if !empty($coreJournal.ppn)}
           &nbsp;&nbsp;&nbsp;<a href="{$url}/Search/Results?lookfor={$coreJournal.ppn|escape:"url"}&amp;type=part_of">{translate text='allarticles'}</a>
        {/if}
      </td>
    </tr>
    {/if}
  
    {* Ausgabe *}
    {if !empty($coreEdition)}
    <tr valign="top" class="coreEdition">
      <th>{translate text='Edition'}: </th>
      <td>
        {$coreEdition|escape}
      </td>
    </tr>
    {/if}
    
    {* Maßstab *}
    {if !empty($coreScale)}
    <tr valign="top" class="coreScale">
      <th>{translate text='Maßstab'}: </th>
      <td>
        {$coreScale|escape}
      </td>
    </tr>
    {/if}
    
    {* Veröffentlicht *}
    {if !empty($corePublications)}
    <tr valign="top" class="corePublished">
      <th>{translate text='Published'}: </th>
      <td>
        {foreach from=$corePublications item=field name=loop}
          {$field.0|escape}{if !empty($field.1)}. - {$field.1|escape}{/if}<br/>
        {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Erscheinungsverlauf *}
    {if !empty($coreTracking)}
    <tr valign="top" class="coreTracking">
      <th>{translate text='Erscheinungsverlauf'}: </th>
      <td>
        {foreach from=$coreTracking item=field name=loop}
           {$field|escape}{if !$smarty.foreach.loop.last}; {/if}
        {/foreach}   
      </td>
    </tr>
    {/if}
    
    {* Umfang *}
    {if !empty($coreExtent)}
    <tr valign="top" class="coreExtent">
      <th>{translate text='Umfang'}: </th>
      <td>
        {$coreExtent.a|escape}{if $coreExtent.b} : {$coreExtent.b|escape}{/if}{if $coreExtent.c} ; {$coreExtent.c|escape}{/if}{if $coreExtent.e} + {$coreExtent.e|escape}{/if}
      </td>
    </tr>
    {/if}
    
    {* Format *}
    <tr valign="top">
      <th>{translate text='Format'}: </th>
      <td>
       {if is_array($recordFormat)}
        {foreach from=$recordFormat item=displayFormat name=loop}
          <span class="iconlabel {$displayFormat|lower|regex_replace:"/[^a-z0-9]/":""}">{translate text=$displayFormat}</span>
        {/foreach}
      {else}
        <span class="iconlabel {$recordFormat|lower|regex_replace:"/[^a-z0-9]/":""}">{translate text=$recordFormat}</span>
      {/if}
      </td>
    </tr>
    
    {* Sprache *}
    <tr valign="top" class="recordLanguage">
      <th>{translate text='Language'}: </th>
      <td>{foreach from=$recordLanguage item=lang name=loop}
             {translate text=$lang|escape}{if !$smarty.foreach.loop.last}; {/if}
          {/foreach}
      </td>
    </tr>
    
    {* Serie / Mehrbändiges Werk *}
    {if !empty($coreNewSeries.1) || !empty($coreNewSeries.3) || !empty($coreNewSeries.4) || !empty($coreNewSeries.5)}
    <tr valign="top" class="coreNewSeries">
      <th>{translate text='Series'}: </th>
      <td>
        {foreach item=wert from=$coreNewSeries.1}
        <a href="{$url}/Search/Results?lookfor=HEB{$wert.ppn|escape:"url"}&amp;type=id">{$wert.text1|escape}</a> 
        {foreach item=wert1 from=$coreNewSeries.0}
           : {if !empty($wert1.text1)}{$wert1.text1 | escape}{if !empty($wert1.text2)}, {/if}{/if}{if !empty($wert1.text2)}{$wert1.text2 | escape}{if !empty($wert1.text3)} ; {/if}{/if}{if !empty($wert1.text3)}{$wert1.text3 | escape}{/if}
        {/foreach}
        </br>
        <a href="{$url}/Search/Results?lookfor={$wert.ppn|escape:"url"}&amp;type=part_of">{translate text='allvolumes'}</a>
        <br/>
        {/foreach}
        {foreach item=wert from=$coreNewSeries.3}
        <a href="{$url}/Search/Results?lookfor=HEB{$wert.ppn|escape:"url"}&amp;type=id">{$wert.text1|escape}</a>
        {foreach item=wert1 from=$coreNewSeries.2}
           : {if !empty($wert1.text1)}{$wert1.text1 | escape}{if !empty($wert1.text2)}, {/if}{/if}{if !empty($wert1.text2)}{$wert1.text2 | escape}{if !empty($wert1.text3)} ; {/if}{/if}{if !empty($wert1.text3)}{$wert1.text3 | escape}{/if}
        {/foreach}
        <br/>
        <a href="{$url}/Search/Results?lookfor={$wert.ppn|escape:"url"}&amp;type=part_of">{translate text='allvolumes'}</a>
        <br/>
        {/foreach}
        {foreach item=wert from=$coreNewSeries.4}
        <a href="{$url}/Search/Results?lookfor=HEB{$wert.ppn|escape:"url"}&amp;type=id">{$wert.text1|escape}</a>
        {if !empty($wert.text2)} ; {$wert.text2 | escape}{/if}
        <br/>
        <a href="{$url}/Search/Results?lookfor={$wert.ppn|escape:"url"}&amp;type=part_of">{translate text='allvolumes'}</a>
        <br/>
        {/foreach}
        {foreach item=wert from=$coreNewSeries.5 name=loop}
        {if !empty($wert.text1)}{$wert.text1 | escape}{/if}
        {if !empty($wert.text2)}{$wert.text2 | escape}{/if}
        {if !$smarty.foreach.loop.last}<br/>{/if}
        {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Einheitssachtitel *}
    {if !empty($coreESTitle) || !empty($coreESTitle2)}
    <tr valign="top" class="coreESTitle">
      <th>{translate text='Einheitssachtitel'}: </th>
      <td>
        {$coreESTitle | escape}
        {if !empty($coreESTitle) && !empty($coreESTitle2)}<br>{/if}
        {$coreESTitle2 | escape}
      </td>
    </tr>
    {/if}
    
    {* Hochschulschrift *}
    {if !empty($coreThesis)}
    <tr valign="top" class="coreThesis">
      <th>{translate text='Hochschulschrift'}: </th>
      <td>
        {foreach from=$coreThesis item=field name=loop}
           {$field | escape}{if !$smarty.foreach.loop.last}<br/>{/if} 
        {/foreach}
      </td>
    </tr>
    {/if}
    
    {* RVK *}
    {if !empty($coreRVK)}
    <tr valign="top" class="coreRVK">
      <th>{translate text='RVK-Notation'}: </th>
      <td>
        {foreach from=$coreRVK item=field name=loop}
           <a href="{$url}/Search/Results?lookfor=rvk_full%3A%22{$field | escape}%22&amp;type=allfields">{$field | escape}</a>{if !$smarty.foreach.loop.last} ; {/if} 
        {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Schlagworte *}
    {if !empty($coreSubjects)}
    <tr valign="top" class="coreSubjects">
      <th>{translate text='Subjects'}: </th>
      <td>
        <div class="subjectLine">
        {foreach from=$coreSubjects item = fieldarray name=loop}
           {foreach from=$fieldarray item=fields name=loop1}
              {if $fields.ppn}<a href="{$url}/Search/Results?lookfor=uses_authority%3A{$fields.ppn | escape}&amp;type=allfields">
              {else}<a href="{$url}/Search/Results?join=AND&amp;bool0[]=AND&amp;lookfor0[]={$fields.search | escape}&amp;type0[]=topics&amp;submit=Suchen">{/if}
              {$fields.display|escape:"html"}
              </a>{if !($smarty.foreach.loop.last && $smarty.foreach.loop1.last)} ; {/if}
           {/foreach}      
        {/foreach}  
        </div>
     </td>
     </tr>
     {/if}   
        

    {if !empty($corePrevTitles)}
    <tr valign="top">
      <th>{translate text='Previous Title'}: </th>
      <td>
        {foreach from=$corePrevTitles item=field name=loop}
          <a href="{$url}/Search/Results?lookfor=%22{$field|escape:"url"}%22&amp;type=Title">{$field|escape}</a><br/>
        {/foreach}
      </td>
    </tr>
    {/if}

    {* Display Link to Volumes *}
    {if !empty($coreVolumes)}
    <tr valign="top">
      <th>{translate text='volumes'}: </th>
      <td>
        {foreach key=schluessel item=wert from=$coreVolumes}
           <a href="{$url}/Search/Results?lookfor={$schluessel|escape:"url"}&amp;type=part_of">{translate text=$wert|escape}</a><br/>
        {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Display Journal Bibliographischer Kontext *}
    {if !empty($coreJBibContext)}
    <tr valign="top">
      <th>{translate text='JBibContext'}: </th>
      <td>
        {foreach key=schluessel item=wert from=$coreJBibContext}
        <a href="{$url}/Search/Results?lookfor=HEB{$schluessel|escape:"url"}&amp;type=id">{$wert|escape}</a></br>
        {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Display Rieviews *}
    {if !empty($coreReview)}
    <tr valign="top">
      <th>{translate text='Review'}: </th>
      <td>
        {foreach key=schluessel item=wert from=$coreReview}
        <a href="{$url}/Search/Results?lookfor=HEB{$schluessel|escape:"url"}&amp;type=id">{$wert|escape}</a></br>
        {/foreach}
      </td>
    </tr>
    {/if}
    
    {* Display Rieviewed Item *}
    {if !empty($coreReviewed)}
    <tr valign="top">
      <th>{translate text='Reviewed'}: </th>
      <td>
        {foreach key=schluessel item=wert from=$coreReviewed}
        <a href="{$url}/Search/Results?lookfor=HEB{$schluessel|escape:"url"}&amp;type=id">{$wert|escape}</a></br>
        {/foreach}
      </td>
    </tr>
    {/if}

    {if !empty($coreURLs) || $coreOpenURL}
    <tr valign="top">
      <th>{translate text='Online Access'}: </th>
      <td>
        {foreach from=$coreURLs item=desc key=currentUrl name=loop}
          <a href="{if $proxy}{$proxy}/login?qurl={$currentUrl|escape:"url"}{else}{$currentUrl|escape}{/if}">{$desc|escape}</a><br/>
        {/foreach}
        {if $coreOpenURL}
          {include file="Search/openurl.tpl" openUrl=$coreOpenURL}<br/>
        {/if}
      </td>
    </tr>
    {/if}

    {if !empty($coreRecordLinks)}
    {foreach from=$coreRecordLinks item=coreRecordLink}
    <tr valign="top">
      <th>{translate text=$coreRecordLink.title}: </th>
      <td><a href="{$coreRecordLink.link|escape}">{$coreRecordLink.value|escape}</a></td>
    </tr>
    {/foreach}
    {/if}
    
    {if !empty($coreTitleLinks)}
    {foreach from=$coreTitleLinks item=coreTitleLink}
    <tr valign="top">
      <th>{translate text=$coreTitleLink.title}: </th>
      <td><a href="{$coreTitleLink.link|escape}">{$coreTitleLink.value|escape}</a></td>
    </tr>
    {/foreach}
    {/if}

<!--{* Temporarily disabled --HeBIS
    <tr valign="top">
      <th>{translate text='Tags'}: </th>
      <td>
        <span style="float:right;">
          <a href="{$url}/Record/{$id|escape:"url"}/AddTag" class="tool add tagRecord" title="{translate text='Add Tag'}" id="tagRecord{$id|escape}">{translate text='Add Tag'}</a>
        </span>
        <div id="tagList">
          {if $tagList}
            {foreach from=$tagList item=tag name=tagLoop}
          <a href="{$url}/Search/Results?tag={$tag->tag|escape:"url"}">{$tag->tag|escape:"html"}</a> ({$tag->cnt}){if !$smarty.foreach.tagLoop.last}, {/if}
            {/foreach}
          {else}
            {translate text='No Tags'}, {translate text='Be the first to tag this record'}!
          {/if}
        </div>
      </td>
    </tr>
*}-->
  </table>
  {* End Main Details *}
</div>

<div class="span-4 text-center last">
  {* Display Cover Image *}
  {if $coreThumbMedium}
    {if $coreThumbLarge}<a href="{$coreThumbLarge|escape}">{/if}
      <img alt="{translate text='Cover Image'}" class="recordcover" src="{$coreThumbMedium|escape}"/>
    {if $coreThumbLarge}</a>{/if}
  {else}
    <img src="{$path}/bookcover.php" class="recordcover" alt="{translate text='No Cover Image'}"/>
  {/if}
  {* End Cover Image *}

  <!-- {* Display the lists that this record is saved to *}
  <div class="savedLists info hide" id="savedLists{$id|escape}">
    <strong>{translate text="Saved in"}:</strong>
  </div>  -->
  
  {if $showPreviews && (!empty($holdingLCCN) || !empty($isbn) || !empty($holdingArrOCLC))}
    {if $showGBSPreviews}
      <div class="previewDiv">
        <a title="{translate text='Preview from'} Google Books" class="hide previewGBS{if $isbn} ISBN{$isbn}{/if}{if $holdingLCCN} LCCN{$holdingLCCN}{/if}{if $holdingArrOCLC} OCLC{$holdingArrOCLC|@implode:' OCLC'}{/if}" target="_blank">
          <!--<img src="https://www.google.com/intl/en/googlebooks/images/gbs_preview_button1.png" alt="{translate text='Preview'}"/>-->
          <img src="{$path}/images/preview_gbs.png" alt="{translate text='Preview'}" class="imagelarge"/>
          <img src="{$path}/images/preview_gbs_small.png" alt="{translate text='Preview'}" class="imagesmall"/>
        </a>
      </div>
    {/if}
    {if $showOLPreviews}
      <div class="previewDiv">
        <a title="{translate text='Preview from'} Open Library" href="" class="hide previewOL{if $isbn} ISBN{$isbn}{/if}{if $holdingLCCN} LCCN{$holdingLCCN}{/if}{if $holdingArrOCLC} OCLC{$holdingArrOCLC|@implode:' OCLC'}{/if}" target="_blank">
          <img src="{$path}/images/preview_ol.gif" alt="{translate text='Preview'}"/>
        </a>
      </div>
    {/if}
    {if $showHTPreviews}
      <div class="previewDiv">
        <a title="{translate text='Preview from'} HathiTrust" class="hide previewHT{if $isbn} ISBN{$isbn}{/if}{if $holdingLCCN} LCCN{$holdingLCCN}{/if}{if $holdingArrOCLC} OCLC{$holdingArrOCLC|@implode:' OCLC'}{/if}" target="_blank">
          <img src="{$path}/images/preview_ht.gif" alt="{translate text='Preview'}"/>
        </a>
      </div>
    {/if}

    {if $showLIBPreviews}
      <div class="previewDiv">
        <a title="{translate text='Preview from'} Libreka" href="" class="hide previewLIB{if $isbn} ISBN{$isbn}{/if}{if $holdingLCCN} LCCN{$holdingLCCN}{/if}{if $holdingArrOCLC} OCLC{$holdingArrOCLC|@implode:' OCLC'}{/if}" target="_blank">
          <img src="{$path}/images/preview_lib.png" alt="{translate text='Preview'}" class="imagelarge"/>
          <img src="{$path}/images/preview_lib_small.png" alt="{translate text='Preview'}" class="imagesmall"/>
        </a>
      </div>
    {/if}
    <span class="previewBibkeys{if $isbn} ISBN{$isbn}{/if}{if $holdingLCCN} LCCN{$holdingLCCN}{/if}{if $holdingArrOCLC} OCLC{$holdingArrOCLC|@implode:' OCLC'}{/if}"></span>
  {/if}
</div>
  
<div class="clear"></div>
