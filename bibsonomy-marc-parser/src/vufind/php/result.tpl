<div class="result recordId" id="record{$summId|escape}">
  <div class="{if $module == 'Discover'}span-100{else}span-3{/if}">
    {foreach from=$summFormats item=format}
      <div class="iconlabel {$format|lower|regex_replace:"/[^a-z0-9]/":""}">{translate text=$format}</div>
    {/foreach}
    
    {if !$summOpenUrl && empty($summURLs) && $summAjaxStatus}
      <div class="ajax_availability hide status" id="status{$summId|escape}">{translate text='Loading'}...</div>

    {* Display Link to full text *}
    {elseif !empty($summURLs)}
      {if $summOpenUrl}
        {include file="Search/openurl.tpl" openUrl=$summOpenUrl}
      {/if}
      {foreach from=$summURLs key=recordurl item=urldesc name=loop}
        {* nur den ersten Link zeigen *}
        {if $smarty.foreach.loop.first}
        <div class="displaylink"><a href="{if $proxy}{$proxy}/login?qurl={$recordurl|escape:"url"}{else}{$recordurl|escape}{/if}" class="fulltext" target="new">{if $recordurl == $urldesc}{translate text='Get full text'}{else}{$urldesc|escape}{/if}</a></div>
        {/if}
      {/foreach}
    
    {* Display Link to Volumes *}
    {elseif !empty($summVolumes)}
    	{foreach key=schluessel item=wert from=$summVolumes}
    	   {assign var="bandzaehlung1" value="strdist(\""}
    	   {assign var="bandzaehlung2" value="\", sortstring, de.hebis.it.solrmods.spell.MemberSort) asc"}
            <div class="displaylink"><a href="{$url}/Search/Results?lookfor={$schluessel|escape:"url"}&amp;type=part_of&amp;sort=part_of_{$schluessel}+asc">{translate text=$wert|escape}</a></div>
        {/foreach}
    {else}
    
    {* Display Link to Record Display for journals and link to record display for retro*}
    {foreach from=$summFormats item=format}
      {if $format eq "journal"}
        <div class="displaylink"><a href="{$url}/Record/{$summId|escape:"url"}#tabnav">{translate text='holding'}</a></div>
      {elseif $format eq "retro"}
        <div class="displaylink"><a href="{$url}/Record/{$summId|escape:"url"}#tabnav">{translate text='siehe Vollanzeige'}</a></div>
      {else}
        <div class="displaylink"><a href="{$url}/Record/{$summId|escape:"url"}#tabnav">{translate text='siehe Vollanzeige'}</a></div>
      {/if}
    {/foreach}
    {/if}
  </div> 

  <div class="{if $module == 'Discover'}span-6{else}span-7{/if}">
    <div class="resultItemLine1">
      <a class="link" href="{$url}/Record/{$summId|escape:"url"}" class="title">
         {if !empty($summPretitle)}[{$summPretitle | escape}]<br>{/if}
         {if !empty($summHighlightedTitle)}{$summHighlightedTitle|addEllipsis:$summTitle|highlight}
         {elseif !$summTitle}{translate text='Title not available'}
         {else}{$summTitle|truncate:100:"..."|escape}
         {/if}
         {if !empty($summShortSubtitle)}<br>{$summShortSubtitle|truncate:100:"..."|escape}{/if}</a>
      {if !empty($summTitleSection) && ($summTitleSectionType == "Teil")}
         {foreach key=schluessel item=wert from=$summTitleSection}
            <br>{$wert.n|escape}{if !empty($wert.p) && !empty($wert.n)}. {/if}{$wert.p|escape}
         {/foreach}  
      {/if}
    </div>

    <div class="resultItemLine2">
      {if !empty($summAuthor)}
        <div class="authornolink">{if !empty($summHighlightedAuthor)}{$summHighlightedAuthor|highlight}{else}{$summAuthor|escape}{/if}</div>
        <a class="link authorlink" href="{$url}/Author/Home?author={$summAuthor|escape:"url"}">{if !empty($summHighlightedAuthor)}{$summHighlightedAuthor|highlight}{else}{$summAuthor|escape}{/if}</a>
      {/if}
      {if empty($summAuthor) and !empty($summPartAuthors)}
        {foreach from=$summPartAuthors item=field name=loop}
          {if $smarty.foreach.loop.first}
          <div class="authornolink">{$field.a|escape}</div>
          <a class="link authorlink" href="{$url}/Author/Home?author={$field.a|escape:"url"}">{$field.a|escape}</a>{if $field.e} ({$field.e|escape}){/if}
          {/if}
        {/foreach}
      {/if}
      {if empty($summAuthor) and empty($summPartAuthors) and !empty($coreCorporation)}
      {foreach from=$coreCorporation item=wert name=loop}
          <div class="authornolink">{$wert|escape:"html"}</div> 
      {/foreach}
      {/if}
      
      
      {if !empty($summJournal)}
       <br>
       {if !empty($summJournal.name)}
        {$summJournal.name|escape}
        {/if}
        {if !empty($summJournal.band)}{$summJournal.band | escape:"html"}{/if}
        {if !empty($summJournal.jahr)} ({$summJournal.jahr | escape:"html"}){/if}{if !empty($summJournal.kommentar)}, {$summJournal.kommentar | escape:"html"}{/if}{if !empty($summJournal.seite)}, S. {$summJournal.seite | escape:"html"}{/if}
      {/if}
      {if $summEdition}<br>{$summEdition|escape}{/if}
      {if $summDate}<br>{$summDate.0.0|escape}{/if}
    </div>
  </div>

  <div class="span-2 right text-center{if $module != 'Discover'} last{/if}">
      {if $summThumb}
        <img src="{$summThumb|escape}" class="summcover" alt="{translate text='Cover Image'}"/>
      {else}
        <img src="{$path}/bookcover.php" class="summcover" alt="{translate text='No Cover Image'}"/>
      {/if}
  </div>

  <div class="span-3 right text-right">
    {if !$puma}
      <a id="saveRecord{$summId|escape}" href="{$url}/Record/{$summId|escape:"url"}/Save" class="fav tool saveRecord" title="{translate text='Add to favorites'}">{translate text='Add to favorites'}</a><br />
    {/if}
    {if $puma AND $loggedIn}
        <a id="saveRecord{$summId|escape}" href="{$url}/Puma/Save?id={$summId|escape:"url"}" class="fav tool saveRecord {$summId|regex_replace:"/\.|\|/":"-"}" title="{translate text='Add to favorites_puma'}">{translate text='Add to favorites_puma'}</a>
    {/if}
    {if $puma AND !$loggedIn}
        <a id="saveRecord{$summId|escape}" style="filter: alpha(opacity=33); opacity:0.33;" rel="notLoggedIn|{translate text='You must be logged in first'}" href="{$url}/Puma/{$summId|escape:"url"}/Save" class="fav tool saveRecord" title="{translate text='Add to favorites_puma'}">{translate text='Add to favorites_puma'}</a>
    {/if}
    {if $showexportfunction}
    <!--<div> 
       <a href="{$url}/Record/{$summId|escape:"url"}/Export?style=EndNote" class="export exportMenuResult">{translate text="Export Record"}</a>
        <ul class="menu offscreen" id="exportMenu" style="list-style-type:none">
          <li><a {if $exportFormat=="RefWorks"}target="{$exportFormat}Main"{else} target="_blank"{/if} href="http://{$pumaHost}/render?format=bib&post.resource.bibtexKey={$bibtex->bibtexKey|escape:"url"}&post.resource.entrytype={$bibtex->entrytype|escape:"url"}&post.resource.title={$bibtex->title|escape:"url"}&post.resource.author={$bibtex->author|escape:"url"}&post.resource.year={$bibtex->year|escape:"url"}&post.resource.abstract={$bibtex->abstract|truncate:300:"..."|escape:"url"}">{translate text="Export to"} BibTeX</a></li>
          <li><a {if $exportFormat=="RefWorks"}target="{$exportFormat}Main"{else} target="_blank"{/if} href="http://{$pumaHost}/render?format=endnote&post.resource.bibtexKey={$bibtex->bibtexKey|escape:"url"}&post.resource.entrytype={$bibtex->entrytype|escape:"url"}&post.resource.title={$bibtex->title|escape:"url"}&post.resource.author={$bibtex->author|escape:"url"}&post.resource.year={$bibtex->year|escape:"url"}&post.resource.abstract={$bibtex->abstract|truncate:300:"..."|escape:"url"}">{translate text="Export to"} {translate text="Literaturverwaltung"}</a></li>
      </li> 
    </div> -->
    
    <div>
      <select onChange="javascript: if(this.value != 'no') window.open(this.value);">
        <option value="no">Exportieren</option>
        <option value="http://{$pumaHost}/render?format=bib&post.resource.bibtexKey={$bibtex->bibtexKey|escape:"url"}&post.resource.entrytype={$bibtex->entrytype|escape:"url"}&post.resource.title={$bibtex->title|escape:"url"}&post.resource.author={$bibtex->author|escape:"url"}&post.resource.year={$bibtex->year|escape:"url"}&post.resource.abstract={$bibtex->abstract|truncate:300:"..."|escape:"url"}">BibTeX</option>
        <option value="http://{$pumaHost}/render?format=endnote&post.resource.bibtexKey={$bibtex->bibtexKey|escape:"url"}&post.resource.entrytype={$bibtex->entrytype|escape:"url"}&post.resource.title={$bibtex->title|escape:"url"}&post.resource.author={$bibtex->author|escape:"url"}&post.resource.year={$bibtex->year|escape:"url"}&post.resource.abstract={$bibtex->abstract|truncate:300:"..."|escape:"url"}">Endnote</option>
      </select>
    </div>
    {/if}
    {*if $showsavedinpuma*}
    {* Display the lists that this record is saved to *}
     <div class="haken savedLists hide" id="savedLists{$summId|escape}">
      <!-- <strong>{translate text="Saved in"}:</strong> -->
     </div>
    {*/if*}
  </div>
  
  <div class="clear"></div>
</div>

</li>
<li class="ui-accordion-content">
  <span class="span-0">&nbsp;</span>
  <div class="result recordIdContent">
    <div class="span-3">&nbsp;
    <!--{*
    {if $showPreviews}
      {if (!empty($summLCCN) || !empty($summISBN) || !empty($summOCLC))}
      <div>
        {if $showGBSPreviews}
          <div class="previewDiv">
            <a title="{translate text='Preview from'} Google Books" class="hide previewGBS{if $summISBN} ISBN{$summISBN}{/if}{if $summLCCN} LCCN{$summLCCN}{/if}{if $summOCLC} OCLC{$summOCLC|@implode:' OCLC'}{/if}" target="_blank">
              <img src="https://www.google.com/intl/en/googlebooks/images/gbs_preview_button1.png" alt="{translate text='Preview'}"/>
            </a>
          </div>
        {/if}
        {if $showOLPreviews}
          <div class="previewDiv">
            <a title="{translate text='Preview from'} Open Library" class="hide previewOL{if $summISBN} ISBN{$summISBN}{/if}{if $summLCCN} LCCN{$summLCCN}{/if}{if $summOCLC} OCLC{$summOCLC|@implode:' OCLC'}{/if}" target="_blank">
              <img src="{$path}/images/preview_ol.gif" alt="{translate text='Preview'}"/>
            </a>
          </div>
        {/if}
        {if $showHTPreviews}
          <div class="previewDiv">
            <a title="{translate text='Preview from'} HathiTrust" class="hide previewHT{if $summISBN} ISBN{$summISBN}{/if}{if $summLCCN} LCCN{$summLCCN}{/if}{if $summOCLC} OCLC{$summOCLC|@implode:' OCLC'}{/if}" target="_blank">
              <img src="{$path}/images/preview_ht.gif" alt="{translate text='Preview'}"/>
            </a>
          </div>
        {/if}
        <span class="previewBibkeys{if $summISBN} ISBN{$summISBN}{/if}{if $summLCCN} LCCN{$summLCCN}{/if}{if $summOCLC} OCLC{$summOCLC|@implode:' OCLC'}{/if}"></span>
      </div>
      {/if}
    {/if}
    *}-->
    </div>

    <div class="span-11">
      <!--{*
      <div class="last">
        {if !empty($summSnippetCaption)}<strong>{translate text=$summSnippetCaption}:</strong>{/if}
        {if !empty($summSnippet)}<span class="quotestart">&#8220;</span>...{$summSnippet|highlight}...<span class="quoteend">&#8221;</span><br/>{/if}
        <div id="callnumAndLocation{$summId|escape}">
        {if $summAjaxStatus}
          <strong class="hideIfDetailed{$summId|escape}">{translate text='Call Number'}:</strong> <span class="ajax_availability hide" id="callnumber{$summId|escape}">{translate text='Loading'}...</span><br class="hideIfDetailed{$summId|escape}"/>
          <strong>{translate text='Located'}:</strong> <span class="ajax_availability hide" id="location{$summId|escape}">{translate text='Loading'}...</span>
          <div class="hide" id="locationDetails{$summId|escape}"></div>
        {elseif !empty($summCallNo)}
          <strong>{translate text='Call Number'}:</strong> {$summCallNo|escape}
        {/if}
        </div>
      </div>
      *}-->

      {if $bookBag}
        <label for="checkbox_{$summId|regex_replace:'/[^a-z0-9]/':''|escape}" class="offscreen">{translate text="Select this record"}</label>
        <input id="checkbox_{$summId|regex_replace:'/[^a-z0-9]/':''|escape}" type="checkbox" name="ids[]" value="{$summId|escape}" class="checkbox_ui"/>
        <input type="hidden" name="idsAll[]" value="{$summId|escape}" />
      {/if}
    
      <div class="span-4 last">
        <!--{*
        <a id="saveRecord{$summId|escape}" href="{$url}/Record/{$summId|escape:"url"}/Save" class="fav tool saveRecord" title="{translate text='Add to favorites'}">{translate text='Add to favorites'}</a>
        *}-->

        {* Display the lists that this record is saved to }
        <div class="savedLists info hide" id="savedLists{$summId|escape}">
          <strong>{translate text="Saved in"}:</strong>
        </div>
        *}
      </div>

      {if $summCOinS}<span class="Z3988" title="{$summCOinS|escape}"></span>{/if}
    </div>

    <div class="clear"></div>
  </div>





