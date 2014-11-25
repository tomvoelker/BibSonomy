<?php
/*
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
 require_once 'RecordDrivers/MarcoriginalRecord.php';
 require_once 'sys/PicaRecord.php';
 /**
 * MARC Record Driver
 *
 * This class is designed to handle MARC records.  Much of its functionality
 * is inherited from the default index-based driver.
 *
 * @category VuFind
 * @package  RecordDrivers
 * @author   
 * @license  http://opensource.org/licenses/gpl-2.0.php GNU General Public License
 * @link     http://vufind.org/wiki/other_than_marc Wiki
 */
class MarcRecord extends MarcoriginalRecord
{
	protected $rawrecord;
	protected $picaarray;
	
    /**
     * Constructor.  We build the object using all the data retrieved 
     * from the (Solr) index (which also happens to include the 
     * 'fullrecord' field containing raw metadata).  Since we have to 
     * make a search call to find out which record driver to construct, 
     * we will already have this data available, so we might as well 
     * just pass it into the constructor.
     *
     * @param array $record All fields retrieved from the index.
     *
     * @access public
     */
    public function __construct($record)
    {
        // Call the parent's constructor...
        parent::__construct($record);
        
        // if raw record format is pica plus then process the pica record
        $this->rawrecord = trim($record['raw_fullrecord']);
        $tmp = new PicaRecord($this->rawrecord);
        $this->picaarray = $tmp;
    }
    
    /**
     * Return the first valid ISBN found in the record (favoring ISBN-10 over
     * ISBN-13 when possible).
     *
     * @return mixed
     * @access protected
     */
    public function getCleanISBN()
    {
    	include_once 'sys/ISBN.php';
    
    	// Get all the ISBNs and initialize the return value:
    	$isbns = $this->getISBNs();
    	$isbn10 = false;
    
    	// Loop through the ISBNs:
    	foreach ($isbns as $isbn) {
    		// Strip off any unwanted notes:
    		if ($pos = strpos($isbn, ' ')) {
    			$isbn = substr($isbn, 0, $pos);
    		}
    
    		// If we find an ISBN-13, return it immediately; otherwise, if we find
    		// an ISBN-10, save it if it is the first one encountered. --HeBIS
    		$isbnObj = new ISBN($isbn);
    		if ($isbn13 = $isbnObj->get13()) {
    			return $isbn13;
    		}
    		if (!$isbn10) {
    			$isbn10 = $isbnObj->get10();
    		}
    	}
    	return $isbn10;
    }    
    
    /**
     * Get an array of all the formats associated with the record.
     *
     * @return array
     * @access protected
     */
    public function getFormats()
    {
    	/* Möglichkeiten von blueprint
    	.manuscript
    	.ebook
    	.book
    	.journal
    	.newspaper
    	.software
    	.physicalobject
    	.cd
    	.dvd
    	.electronic
    	.map
    	.globe
    	.slide
    	.microfilm
    	.photo
    	.video
    	.kit
    	.musicalscore
    	.sensorimage
    	.audio
    	*/
    	
    	// Im Moment ausgewertete Materialart
    	$materialart = array(array(array()));
    	$materialart["a"]["m"]["xxx"]="book";
    	$materialart["a"]["m"]["co"]="dvd";
    	$materialart["a"]["m"]["cocd"]="cd";
    	$materialart["a"]["m"]["c "]="cd";
    	$materialart["a"]["m"]["cr"]="ebook";
    	$materialart["a"]["m"]["cu"]="ebook";
    	$materialart["a"]["m"]["h"]="microfilm";
    	$materialart["a"]["m"]["f"]="braille";
    	$materialart["a"]["m"]["o"]="kit";
    	$materialart["a"]["s"]["xxx"]="journal";
    	$materialart["a"]["s"]["t"]="journal";
    	$materialart["a"]["s"]["h"]="journal";
    	$materialart["a"]["s"]["co"]="journal";
    	$materialart["a"]["s"]["cocd"]="journal";
    	$materialart["a"]["s"]["cr"]="electronic";
    	$materialart["a"]["s"]["f"]="braille";
    	$materialart["c"]["m"]["q"]="musicalscore";
    	$materialart["c"]["s"]["q"]="musicalscore";
    	$materialart["e"]["m"]["a"]="map";
    	$materialart["e"]["s"]["a"]="map";
    	$materialart["g"]["m"]["m"]="video";
    	$materialart["g"]["m"]["xxx"]="video";
    	$materialart["g"]["s"]["m"]="video";
    	$materialart["g"]["s"]["xxx"]="video";
    	$materialart["i"]["m"]["s"]="audio";
    	$materialart["i"]["m"]["cocd"]="cd";
    	$materialart["j"]["m"]["xxx"]="audio";
    	$materialart["j"]["m"]["s"]="audio";
    	$materialart["j"]["m"]["cocd"]="audio";
    	$materialart["j"]["s"]["co"]="audio";
    	$materialart["j"]["s"]["s"]="audio";
    	$materialart["k"]["m"]["a"]="photo";
    	$materialart["k"]["m"]["k"]="photo";
    	$materialart["o"]["m"]["xxx"]="kit";
    	$materialart["o"]["m"]["o"]="kit";
    	$materialart["r"]["m"]["xxx"]="physicalobject";
    	$materialart["r"]["m"]["z"]="physicalobject";
    	$materialart["t"]["m"]["xxx"]="manuscript";
    		
    	// format ist is detected by infos in Leader and kat 007
    	$leader = $this->marcRecord->getLeader();
    	$fields = $this->marcRecord->getFields("007", false);
    	$phys = array();
    	$tmp = $this->_getFirstFieldValue('300', array('a'));
    	
    	if ($fields){
    	   foreach($fields as $field){
    	      $data = $field->getData();
    	      if ($data[0] === 'c'){
    	         // cd or dvd
    	         if ($data[0].$data[1] === 'co' && (strpos(strtoupper($tmp), 'DVD') === false )) {
    	      	    $phys[] = 'cocd';
    	         }
    	         else 
    	         	$phys[] = $data[0].$data[1];
    	      }   
    	      else
    	   	     $phys[] = $data[0];
    	      }
    	}   
    	else {
    		$phys[]="xxx";
    	} 
    	   	   
    	$art = $leader[6];
    	$level = $leader[7];
    	
    	// now we have the three components art, level and phys. 
    	//For some formats this is not enough and we need additional infos
    	
    	// preliminary solution for detection of series
    	$tmp = $this->picaarray->getTit();
    	if (strpos($tmp['002@']['$0'], "c") === 1 ||
    			strpos($tmp['002@']['$0'], "d") === 1 ){
    		return array("series");
    	}
    	
    	// preliminary solution for articles
        $tmp = $this->picaarray->getTit();
    	if (strpos($tmp['002@']['$0'], "o") === 1 ){
    		return array("article");
    	}
    	
    	// preliminary solution for retro
    	$tmp = $this->picaarray->getTit();
    	if (strpos($tmp['002@']['$0'], "r") === 0 ){
    		return array("retro");
    	}
    	  	
    	// return formats accourding to format arry in the beginning
    	// of this method
    	foreach($phys as $p){
    		if (isset( $materialart[$art][$level][$p] )){
         		return array($materialart[$art][$level][$p]);
         	}
        }

    	// there is no format defined for the combination of art level and phys
    	// for debugging
    	return array("misc");
    }
    
    /**
     * Get an array of all ISBNs associated with the record (may be empty).
     *
     * @return array
     * @access protected
     */
    protected function getISBNs()
    {
    	$tmp1 = $this->_getFieldArray('020', array('a'));
    	$tmp2 = $this->_getFieldArray('020', array('z'));
    	return array_merge($tmp1, $tmp2);
    }
    
    /**
     * Get an array of all ISSNs associated with the record (may be empty).
     *
     * @return array
     * @access protected
     */
    protected function getISSNs()
    {
    	$tmp1 = $this->_getFieldArray('022', array('a'));
    	$tmp2 = $this->_getFieldArray('022', array('y'));
    	$tmp3 = $this->_getFieldArray('029', array('a'));
    	return array_merge($tmp1, $tmp2, $tmp3);
    } 
    
    /**
     * Get the main author of the record.
     *
     * @return string
     * @access protected
     */
    public function getPrimaryAuthor()
    {
    	$tmp = trim($this->_getFirstFieldValue('100', array('a')));
    	
    	if(strlen(trim($this->_getFirstFieldValue('100', array('b'))))>0){
    	   $tmp = $tmp." ".trim($this->_getFirstFieldValue('100', array('b')));
    	}
    	if(strlen(trim($this->_getFirstFieldValue('100', array('c'))))>0){
    		$tmp = $tmp." <".trim($this->_getFirstFieldValue('100', array('c'))).">";
    	}   
    	
    	return $tmp;   	
    }
    
    /**
     * Get the edition of the current record.
     *
     * @return string
     * @access protected
     */
    protected function getEdition()
    {
    	return $this->_getFirstFieldValue('250', array('a'));
    }
    
    /**
     * Get the Verlauf (tracking?) of the current record.
     *
     * @return string
     * @access protected
     */
    protected function getTracking()
    {
    	return $this->_getFieldArray('362', array('a'));
    }
    
    /**
     * Get the scale of the current record.
     *
     * @return string
     * @access protected
     */
    protected function getScale()
    {
    	return $this->_getFirstFieldValue('255', array('a'));
    }
    
    /**
     * Get the extent of the current record.
     *
     * @return string
     * @access protected
     */
    protected function getExtent()
    {
    	return array('a' => $this->_getFirstFieldValue('300', array('a')),
    	             'b' => $this->_getFirstFieldValue('300', array('b')),
    	             'c' => $this->_getFirstFieldValue('300', array('c')),
    	             'e' => $this->_getFirstFieldValue('300', array('e')));
    }
    
    /**
     * Get an array of all the languages associated with the record.
     *
     * @return array
     * @access protected
     */
    protected function getLanguages()
    {
    	$tmp = array();
    	$fields = $this->marcRecord->getFields('041');
    	foreach ($fields as $field){
    		$allSubfields = $field->getSubfields();
    		foreach ($allSubfields as $currentSubfield){
    			$code = trim($currentSubfield->getCode());
    			$data = trim($currentSubfield->getData());
    			if (strcmp($code, 'a')===0){
    				$tmp[] = $data;
    			}
    			
    		}
    	}
    	return $tmp;
    }
    
    /**
     * Get the full title of the record.
     *
     * @return string
     * @access protected
     */
    protected function getTitle()
    {
    	// 245 $a
    	$tmp = ''; 
    	$tmp1 = $this->_getFieldArray('245', array('a'), false);
    	if (count($tmp1)> 0){
    	   $tmp = $tmp1[0];
    	}
    	
    	// Sortierzeichen weg
    	if (strpos($tmp, '@') !== false){
    		$occurrence = strpos($tmp, '@');
    		$tmp = substr_replace($tmp, '', $occurrence, 1);
    	}
    	
    	return $tmp;
    }
    
    /**
     * Get the full title of the record for hitlist.
     *
     * @return string
     * @access protected
     */
    protected function getTitle2()
    {
    	// 245 $a
    	$tmp = '';
    	$tmp1 = $this->_getFieldArray('245', array('a'), false);
    	if (count($tmp1)> 0){
    		$tmp = $tmp1[0];
    	}
    	
    	$tmp2 = $this->_getFieldArray('245', array('h'), false);
    	if (count($tmp2)> 0){
    		if (strlen($tmp)>0){
    			$tmp = $tmp.' ';
    		}
    		$tmp = $tmp.$tmp2[0];
    	}
    	 
    	// Sortierzeichen weg
    	if (strpos($tmp, '@') !== false){
    		$occurrence = strpos($tmp, '@');
    		$tmp = substr_replace($tmp, '', $occurrence, 1);
    	}
    	 
    	return $tmp;
    }
    
    /**
     * Get the publication dates of the record.  See also getDateSpan().
     *
     * @return array
     * @access protected
     */
    protected function getPlacesOfPublication()
    {
    	$fields = $this->marcRecord->getFields('260');
    	$tmp = array();
    	$base = '';
    	$zusatz = '';
    	$a ='';
    	$b ='';
    	$c ='';
    	$e ='';
    	$f ='';
    	
    	foreach ($fields as $field){
    		if (strcmp($field->getIndicator(1), '3')===0){
    			$allSubfields = $field->getSubfields();

    			foreach ($allSubfields as $currentSubfield) {
    				$code = trim($currentSubfield->getCode());
    				$data = trim($currentSubfield->getData());

    				if (strcmp($code, 'a')===0) {
    					$a=$data;
    				}
    				if (strcmp($code, 'b')===0) {
    					$b=$data;
    				}
    				if (strcmp($code, 'c')===0) {
    					$c=$data;
    				}
    				if (strcmp($code, 'e')===0) {
    					$e=$data;
    				}
    				if (strcmp($code, 'f')===0) {
    					$f=$data;
    				}

    			}
    			
    			$base = $a;
    			
    			if (strlen($b)> 0){
    				if (strlen($base) > 0 ){
    					$base = $base.' : ';
    				}
    				$base = $base.$b;
    			}
    			if (strlen($c)> 0){
    				if (strpos($c, ',') !== false){
    					$occurrence = strpos($c, ',');
    					$c = substr($c, $occurrence+1);
    				}
    				if (strlen($base) > 0 ){
    					$base = $base.', ';
    				}
    				$base = $base.$c;
    			}
    		    if (strlen($e)> 0){
    				$zusatz = $zusatz.$e;
    			}
    			if (strlen($f)> 0){
    				if (strlen($zusatz) > 0 ){
    					$zusatz = $zusatz.', ';
    				}
    				$zusatz = $zusatz.$f;
    			}
    			if (strlen($zusatz)> 0){
    				$zusatz = '('.$zusatz.')';
    			}
                $tmp[] = array($base, $zusatz);
                $base = '';
                $zusatz = '';
                $a ='';
                $b ='';
                $c ='';
                $e ='';
                $f ='';
    		}
   
    	}
    	return $tmp;   
    }
    
    /**
     * Get an array of search results for other editions of the title
     * represented by this record (empty if unavailable).  In most cases,
     * this will use the XISSN/XISBN logic to find matches.
     *
     * @return mixed Editions 
     * @access public
     */
    public function getEditions()
    {
    	include_once 'sys/WorldCatUtils.php';
    	
    	$wc = new WorldCatUtils();
    	$filter = array();
    	
    	$searchSettings = getExtraConfigArray('searches');
        
    	if (isset($searchSettings['HiddenFilters'])) {
            foreach ($searchSettings['HiddenFilters'] as $field => $subfields) {
                $filter[] = ($field.':'.'"'.$subfields.'"');
            }
        }
    	
    	// Try to build an array of OCLC Number, ISBN or ISSN-based sub-queries:
    	$parts = array();
    	$oclcNum = $this->getCleanOCLCNum();
    	if (!empty($oclcNum)) {
    		$oclcList = $wc->getXOCLCNUM($oclcNum);
    		foreach ($oclcList as $current) {
    			$parts[] = "oclc_num:" . $current;
    		}
    	}
    	$isbn = $this->getCleanISBN();
    	if (!empty($isbn)) {
    		$isbnList = $wc->getXISBN($isbn);
    		foreach ($isbnList as $current) {
    			$parts[] = 'isxn:' . $current;
    		}
    	}
    	$issn = $this->getCleanISSN();
    	if (!empty($issn)) {
    		$issnList = $wc->getXISSN($issn);
    		foreach ($issnList as $current) {
    			$parts[] = 'isxn:' . $current;
    		}
    	}
    
    	// If we have query parts, we should try to find related records:
    	if (!empty($parts)) {
    		// Limit the number of parts based on the boolean clause limit:
    		$index = $this->getIndexEngine();
    		$limit = $index->getBooleanClauseLimit();
    		if (count($parts) > $limit) {
    			$parts = array_slice($parts, 0, $limit);
    		}
    
    		// Assemble the query parts and filter out current record:
    		$query = '(' . implode(' OR ', $parts) . ') NOT id:' .
    				$this->getUniqueID();
    
    		// Perform the search and return either results or an error:
    		$result = $index->search($query, null, $filter, 0, 5);
    		if (PEAR::isError($result)) {
    			return $result;
    		}
    		if (isset($result['response']['docs'])
    				&& !empty($result['response']['docs'])
    		) {
    			// fill new array with results
    			$editions = array();
    			$field = array();
    			for($i=0; $i < count($result['response']['docs']); $i++) {
    				$rec = new MarcRecord($result['response']['docs'][$i]);
    				$field=array("format" => $rec->getFormats(),
    						"id" => $rec->getUniqueId(),
    						"title" => $rec->getTitle(),
    						"edition" => $rec->getEdition(),
    						"publishDate" => $rec->getPlacesOfPublication(),
    						'titleSection',$rec->getTitleSection(),
    						'author', $rec->getPrimaryAuthor(),
    						'partAuthors', $rec->getPartAuthors(),
    						'pretitle', $rec->getPretitle(),
    						'shortSubtitle', $rec->getShortSubtitle());
    				$editions[]=$field;
    				$field = array();
    			}
               return $editions;
    		   //return $result['response']['docs'];
    		}
    	}
    	// If we got this far, we were unable to find any results:
    	return null;
    }

    /**
     * Get an array of all series names containing the record.  Array entries may
     * be either the name string, or an associative array with 'name' and 'number'
     * keys.
     *
     * @return array
     * @access protected
     */
    protected function getSeries()
    {
    	$matches = array();
    	$tmparray = array();
    	$count = '';
    	$f036A = array();
    	$f036B = array();
    	$f036C = array();
    	$f036D = array();
    	$f036F = array();
    	$f036G = array();

    	$tmp = $this->picaarray->getTit();
    	foreach ($tmp as $key=>$value){
    		if (strlen($key) < 7) {
    			$count = '00';
    		}
    		else {
    			$count = substr($key, -2, 2);
    		}
    		if (strpos($key, '036B') === 0) {
    			if (array_key_exists('$9', $value)){
    				$f036B[$count]['ppn'] = $value['$9'];
    			}
    			if (array_key_exists('$8', $value)){
    				$f036B[$count]['text1'] = str_replace("@", "", $value['$8']);
    			}
    		}
    		if (strpos($key, '036A') === 0 ) {
    			if (array_key_exists('$m', $value)){
    				$f036A[$count]['text1'] = $value['$m'];
    			}
    			if (array_key_exists('$a', $value)){
    				$f036A[$count]['text2'] = str_replace("@", "", $value['$a']);
    			}
    			if (array_key_exists('$l', $value)){
    				$f036A[$count]['text3'] = $value['$l'];
    			}
    		}
    		if (strpos($key, '036D') === 0) {
    			if (array_key_exists('$9', $value)){
    				$f036D[$count]['ppn'] = $value['$9'];
    			}
    			if (array_key_exists('$8', $value)){
    				$f036D[$count]['text1'] = str_replace("@", "", $value['$8']);
    			}
    		}
    		if (strpos($key, '036C') === 0 ) {
    			if (array_key_exists('$m', $value)){
    				$f036C[$count]['text1'] = $value['$m'];
    			}
    			if (array_key_exists('$a', $value)){
    				$f036C[$count]['text2'] = str_replace("@", "", $value['$a']);
    			}
    			if (array_key_exists('$l', $value)){
    				$f036C[$count]['text3'] = $value['$l'];
    			}
    		}
    		if (strpos($key, '036F') === 0) {
    			if (array_key_exists('$9', $value)){
    				$f036F[$count]['ppn'] = $value['$9'];
    			}
    			if (array_key_exists('$8', $value)){
    				$f036F[$count]['text1'] = str_replace("@", "", $value['$8']);
    			}
    			if (array_key_exists('$l', $value)){
    				$f036F[$count]['text2'] = $value['$l'];
    			}
    		}
    		if (strpos($key, '036G') === 0) {
    			$f036G[$count]['text2'] = '';
    			if (array_key_exists('$a', $value)){
    				$f036G[$count]['text1'] = $value['$a'];
    			}
    			foreach ($value as $key1=>$value1){
    				if (strpos('$d', $key1)===0){
    					$f036G[$count]['text2'] = $f036G[$count]['text2'].' : '.$value1;
    				}
    			}
    		}
    	}
    	$matches = array($f036A, $f036B, $f036C, $f036D, $f036F, $f036G);
    	return $matches;
    }
    
    /**
     * Get Journal of an Article
     *
     * @return array
     * @access protected
     */
    protected function getJournal()
    {
    	$journal = array();
    	$tmp = $this->picaarray->getTit();

    	$suchmuster1 = '/--.+--:/';
    	$suchmuster2 = '/--.+--/';
    	$ersetzung = '';
    	
    	if (strpos($tmp['002@']['$0'], "o") === 1) {
    		if (isset($tmp['039B']['$9']) !== false ) {
    		    $journal['ppn'] = $tmp['039B']['$9'];
    		}
    		if (isset($tmp['039B']['$a']) !== false ) {
    			$journal['prefix'] = $tmp['039B']['$a'];
    		} 
    		if (isset($tmp['039B']['$8']) !== false ) {
    			$journal['name'] = preg_replace($suchmuster2, $ersetzung, preg_replace($suchmuster1, $ersetzung, $tmp['039B']['$8']));
    		}
    	  if (isset($tmp['039B']['$c']) !== false && !isset($journal['name'])) {
    			$journal['name'] = $tmp['039B']['$c'];	
    		}
        if (isset($tmp['031A']['$d']) !== false ) {
    		    $journal['band'] = $tmp['031A']['$d'];
    		}
        if (isset($tmp['031A']['$j']) !== false ) {
    		    $journal['jahr'] = $tmp['031A']['$j'];
    		}
        if (isset($tmp['031A']['$e']) !== false ) {
    		    $journal['kommentar'] = $tmp['031A']['$e'];
    		}
        if (isset($tmp['031A']['$h']) !== false ) {
    		    $journal['seite'] = $tmp['031A']['$h'];
    		}   
    	}
    	
    	return $journal;
    	
    }
    
    /**
     * Get other Editions of Journal
     *
     * @return array
     * @access protected
     */
    protected function getJOtherEditions()
    {
    	$journal = array();
    	$tmp = $this->picaarray->getTit();
    	$ppn = '';
    	$text = '';
    	
    	$suchmuster1 = '/--.+--:/';
    	$suchmuster2 = '/--.+--/';
    	$ersetzung = '';
        
    	foreach ($tmp as $key=>$value){
    	    if (strpos($key, '039D') === 0) {
    	    if (array_key_exists('$9', $value)){
             $ppn=$value['$9'];
             }
          if (array_key_exists ('$a', $value)) {
             $text = $text.$value['$a'];
          }
          if (array_key_exists ('$8', $value)) {
             $text = $text.': '.preg_replace($suchmuster2, $ersetzung, preg_replace($suchmuster1, $ersetzung, $value['$8']));
          } 
    			$journal[] = array('ppn'=>$ppn, 'text'=>$text);
          $ppn='';
          $text=''; 
    		}
    	}
    	
    	$tmp = $this->_getFieldSubfieldArray('755',  array('i', 't'));
    	foreach ($tmp as $field){
    	   foreach ($field as $subfield){
    				if (strcmp($subfield[0], 'i')===0) {
    				   $text=$text.$subfield[1];
               } 
            if (strcmp($subfield[0], 't')===0) {
    				   $text=$text.': '.$subfield[1];
               }  
            $journal[]=array('ppn'=>$ppn, 'text'=>$text);
            $text = '';
         }       
      }
    
      return $journal;
    
    }
    
    /**
     * Get bibliographical context of Journal
     *
     * @return array
     * @access protected
     */
    protected function getReviewed()
    {
    	$journal = array();
    	$tmp = $this->picaarray->getTit();
    	
    	foreach ($tmp as $key=>$value){
    		if (strpos($key, '039T') === 0) {
    			$journal[$value['$9']] = $value['$8'];
    		}
    	}
    	
    	return $journal;
    		
    }
    
    /**
     * Get bibliographical context of Journal
     *
     * @return array
     * @access protected
     */
    protected function getReview()
    {
    	$journal = array();
    	$tmp = $this->picaarray->getTit();
    
    	foreach ($tmp as $key=>$value){
    		if (strpos($key, '039U') === 0) {
    			$journal[$value['$9']] = $value['$8'];
    		}
    	}
    	
    	return $journal;
    
    }
    /**
     * Get bibliographical context of Journal
     *
     * @return array
     * @access protected
     */
    protected function getJBibContext()
    {
    	$journal = array();
    	$tmp = $this->picaarray->getTit();

    	$suchmuster1 = '/--.+--:/';
    	$suchmuster2 = '/--.+--/';
    	$ersetzung = '';

    	foreach ($tmp as $key=>$value){
    		if ((strpos($tmp['002@']['$0'], "b") === 1 ||
    				strpos($tmp['002@']['$0'], "d") === 1) &&
    				strpos($key, '039B') === 0) {
    			$journal[$value['$9']] = preg_replace($suchmuster2, $ersetzung, preg_replace($suchmuster1, $ersetzung, $value['$8']));
    		}
    		if (strpos($key, '039C') === 0) {
    			$journal[$value['$9']] = preg_replace($suchmuster2, $ersetzung, preg_replace($suchmuster1, $ersetzung, $value['$8']));
    		}
    		if (strpos($key, '039E') === 0 && array_key_exists ('$9', $value) && array_key_exists ('$8', $value)) {
    			$journal[$value['$9']] = preg_replace($suchmuster2, $ersetzung, preg_replace($suchmuster1, $ersetzung, $value['$8']));
    		}
    		if (strpos($key, '039S') === 0) {
    			$journal[$value['$9']] = preg_replace($suchmuster2, $ersetzung, preg_replace($suchmuster1, $ersetzung, $value['$8']));
    		}
    		if (strpos($key, '039X') === 0) {
    			$journal[$value['$9']] = preg_replace($suchmuster2, $ersetzung, preg_replace($suchmuster1, $ersetzung, $value['$8']));
    		}
    	}

    	return $journal;

    }
    
    protected function getVolumes()
    {
    	$volumes = array();
    	$tmp = $this->picaarray->getTit();
    	
    	if (strpos($tmp['002@']['$0'], "c") === 1 ||
    			strpos($tmp['002@']['$0'], "d") === 1 ){
    		$volumes[$tmp['003@']['$0']]='allvolumes';
    	}
    	
    	return $volumes;
    }
    
    /**
     * Assign necessary Smarty variables and return a template name to
     * load in order to display extended metadata (more details beyond
     * what is found in getCoreMetadata() -- used as the contents of the
     * Description tab of the record view).
     *
     * @return string Name of Smarty template file to display.
     * @access public
     */
    public function getExtendedMetadata()
    {
    global $interface;
    
    parent::getExtendedMetadata();
    $interface->assign('extendedOtherEditions', $this->getJOtherEditions());
    $interface->assign('extendedReportNumber', $this->getReportNumber());
    $interface->assign('extendedAnnotation', $this->getAnnotation());
    
    return 'RecordDrivers/Index/extended.tpl';
    }	
    /**
     * Assign necessary Smarty variables and return a template name to
     * load in order to display core metadata (the details shown in the
     * top portion of the record view pages, above the tabs).
     *
     * @return string Name of Smarty template file to display.
     * @access public
     */
    public function getCoreMetadata()
    {
    	global $configArray;
    	global $interface;
        
    	// Assign required variables (some of these are also used by templates for
    	// tabs, since every tab can assume that the core data is already assigned):
    	$this->assignTagList();
    	$interface->assign('isbn', $this->getCleanISBN());  // needed for covers
    	$interface->assign('recordFormat', $this->getFormats());
    	$interface->assign('recordLanguage', $this->getLanguages());
    
    	// These variables are only used by the core template, and they are prefixed
    	// with "core" to prevent conflicts with other variable names.
    	$interface->assign('coreShortTitle', $this->getTitle());
    	$interface->assign('coreSubtitle', $this->getSubtitle());
    	$interface->assign('coreTitleStatement', $this->getTitleStatement());
    	
    	// Teil bzw. Reihe
    	$interface->assign('coreTitleSection', $this->getTitleSection());
    	$interface->assign('coreTitleSectionType', $this->getTitleSectionType());
    	
    	//darin enthalten
    	$interface->assign('coreContained', $this->getContained());
    	
    	// Körperschaft
    	$interface->assign('coreCorporation', $this->getCorporation());
    	
    	// Festschrift
    	$interface->assign('coreFestschrift', $this->getFestschrift());
    	
    	// Interpret
    	$interface->assign('coreInterpreter', $this->getInterpreter());
    	
    	// In
    	$interface->assign('coreJournal', $this->getJournal());
    	
    	// Ausgabe
    	$interface->assign('coreEdition', $this->getEdition());
    	
    	// Maßstab
    	$interface->assign('coreScale', $this->getScale());
    	
    	// Veröffentlicht
    	$interface->assign('corePublications', $this->getPlacesOfPublication());
    	
    	//Verlauf
    	$interface->assign('coreTracking', $this->getTracking());
    	
    	//Umfang
    	$interface->assign('coreExtent', $this->getExtent());
    	
    	//Einheitssachtitel
    	$interface->assign('coreESTitle', $this->getPretitle());
    	$interface->assign('coreESTitle2', $this->getPretitle2());
    	
    	// Hochschulschrift
    	$interface->assign('coreThesis', $this->getThesis());
    	
    	// RVK
    	$interface->assign('coreRVK', $this->getRVK());
    	
    	$interface->assign('coreNextTitles', $this->getNewerTitles());
    	$interface->assign('corePrevTitles', $this->getPreviousTitles());
    	$interface->assign('coreSeries', $this->getSeries());
    	$interface->assign('coreSubjects', $this->getAllSubjectHeadings());
    	$interface->assign('coreRecordLinks', $this->getAllRecordLinks());
    	$interface->assign('coreTitleLinks', $this->getAllTitleLinks());
    	$interface->assign('coreThumbMedium', $this->getThumbnail('medium'));
    	$interface->assign('coreThumbLarge', $this->getThumbnail('large'));
    	$interface->assign('coreNewSeries', $this->getSeries());
    	$interface->assign('coreVolumes', $this->getVolumes());
    	$interface->assign('coreJBibContext', $this->getJBibContext());
    	$interface->assign('coreReview', $this->getReview());
    	$interface->assign('coreReviewed', $this->getReviewed());
    	
    	// Only display OpenURL link if the option is turned on and we have
    	// an ISSN.  We may eventually want to make this rule more flexible,
    	// but for now the ISSN restriction is designed to be consistent with
    	// the way we display items on the search results list.
    	$hasOpenURL = ($this->openURLActive('record') && $this->getCleanISSN());
    	if ($hasOpenURL) {
    		$interface->assign('coreOpenURL', $this->getOpenURL());
    	}
    
    	// Only load URLs if we have no OpenURL or we are configured to allow
    	// URLs and OpenURLs to coexist:
    	if (!isset($configArray['OpenURL']['replace_other_urls'])
    			|| !$configArray['OpenURL']['replace_other_urls'] || !$hasOpenURL
    	) {
    		$interface->assign('coreURLs', $this->getURLs());
    	}
    
    	//Autor
    	// The secondary author array may contain a corporate or primary author;
    	// let's be sure we filter out duplicate values.
    	$mainAuthor = $this->getPrimaryAuthor();
    	$secondaryAuthors = $this->getSecondaryAuthors();
    	
    	$duplicates = array();
    	if (!empty($mainAuthor)) {
    		$duplicates[] = $mainAuthor;
    	}
    	if (!empty($duplicates)) {
    		$secondaryAuthors = array_diff($secondaryAuthors, $duplicates);
    	}
    	$interface->assign('coreMainAuthor', $mainAuthor);
    	$interface->assign('coreSecondaryAuthor', $secondaryAuthors);
    	$interface->assign('corePartAuthor', $this->getPartAuthors());
    	
    	// Assign only the first piece of summary data for the core; we'll get the
    	// rest as part of the extended data.
    	$summary = $this->getSummary();
    	$summary = count($summary) > 0 ? $summary[0] : null;
    	$interface->assign('coreSummary', $summary);
    
      // Data for Copies of the Title
      $interface->assign('coreCopies', $this->getCopies());
      
      /**
       * The following part assigns bibtex objects for the template. This is 
       * neccessary to generate bibtex and endnote export formats
       */
      include_once 'services/Puma/OPACRecord.php';
      include_once 'services/Puma/BibSonomyAPI/utils/BibsonomyPublicationUtils.php';
      
      $puma_rec = OPACRecord::initRecord($this->fields);
      $posts = BibsonomyPublicationUtils::bibsonomyPublication2BibtexStdclass( $puma_rec );
      $interface->assign('bibtex', $posts);
      
    	// Send back the template name:
      return 'RecordDrivers/Index/core.tpl';
    }	
    
    /**
     * Get an array of search results for similar records of the title
     * represented by this record (empty if unavailable). This will
     * use the ID to find matches.
     *
     * @return mixed Similar Records 
     * @access public
     */
    public function getSimilarRecords()
    {
    	// Perform the search and return either results or an error:
    	$index = $this->getIndexEngine();
    	$result = $index->getMoreLikeThis($this->getUniqueId());
    	
    	if (isset($result['response']['docs'])
    			&& !empty($result['response']['docs'])
    	) {
    		// fill new array with results
    		$similar = array();
    		$field = array();
    		for($i=0; $i < count($result['response']['docs']); $i++) {
    			$rec = new MarcRecord($result['response']['docs'][$i]);
    			$field=array("format" => $rec->getFormats(),
    					"id" => $rec->getUniqueId(),
    					"title" => $rec->getTitle(),
    					"author" => $rec->getPrimaryAuthor(),
    					"publishDate" => $rec->getPublicationDates());
    			$similar[]=$field;
    			$field = array();
    		}
    		return $similar;    	
    	}
    	// If we got this far, we were unable to find any results:
    	return null;    	
    }
    
    /**
     * Get the short (pre-subtitle) title of the record.
     *
     * @return string
     * @access protected
     */
    public function getShortTitle()
    {
    	// 245 $a_:_$b
    	$tmp = array(); 
    	$tmp1 = $this->_getFieldArray('245', array('a'), false);
    	if (count($tmp1)> 0){
    	   $tmp = $tmp1[0];
    	}
    	
    	$tmp2 = $this->_getFieldArray('245', array('b'), false);
    	if (count($tmp2)> 0){
    		$tmp = $tmp.' : '.$tmp2[0];
    	}
    	
    	// Sortierzeichen weg
    	if (strpos($tmp, '@') !== false){
    		$occurrence = strpos($tmp, '@');
    		$tmp = substr_replace($tmp, '', $occurrence, 1);
    	}
    	
    	return $tmp;
    }
    
    /**
     * Assign necessary Smarty variables and return a template name to
     * load in order to display the full record information on the Staff
     * View tab of the record view page.
     *
     * @return string Name of Smarty template file to display.
     * @access public
     */
    public function getStaffView()
    {
    	global $interface;
    
    	// Get Record as MARCXML
    	$xml = trim($this->marcRecord->toXML());
    
    	// Prevent unprintable characters from interfering with the XSL transform:
    	$xml = str_replace(
    			array(chr(27), chr(28), chr(29), chr(30), chr(31)), ' ', $xml
    	);
    
    	// Transform MARCXML
    	$style = new DOMDocument;
    	$style->load('services/Record/xsl/record-marc.xsl');
    	$xsl = new XSLTProcessor();
    	$xsl->importStyleSheet($style);
    	$doc = new DOMDocument;
    	if ($doc->loadXML($xml)) {
    		$html = $xsl->transformToXML($doc);
    		$interface->assign('details', $html);
    	}
    	
    	$tmp = $this->picaarray->getTit();
    	$interface->assign('pica', "<pre>".$this->rawrecord."</pre>");
  
    	return 'RecordDrivers/Marc/staff.tpl';
    }
    
    /**
     * Get any table of contents associated with this record.  For details of
     * the return format, see sys/TOC.php. --HeBIS
     *
     * @return array ExternalTOC information.
     * @access public
     */
    public function getExternalTOC()
    {
    	include_once 'sys/TOC.php';
    
    	$ed = new ExternalTOC($this->getCleanISBN());
    	return $ed->fetch();
    }
    
    
    /**
     * Get any summaries associated with this record.  For details of
     * the return format, see sys/Summaries.php. --HeBIS
     *
     * @return array Summary information.
     * @access public
     */
    public function getSummaries()
    {
    	include_once 'sys/Summaries.php';
    
    	$ed = new ExternalSummaries($this->getCleanISBN());
    	return $ed->fetch();
    }    

    /**
     * Does this record have an excerpt available?
     *
     * @return bool
     * @access public
     */
    public function hasExcerpt()
    {
    	// If we have ISBN(s), we might have excerpts:
    	$isbns = $this->getISBNs();
    	
    	// Do we have external excerpts? --HeBIS
    	if (!empty($isbns)) {
    		$excerpts = $this->getExcerpts();
    		if (!empty($excerpts)) {
    		    return true;	
    		}
    	}

    	return false;
    }    
    
    /**
     * Does this record have reviews available?
     *
     * @return bool
     * @access public
     */
    public function hasReviews()
    {
    	// If we have ISBN(s), we might have reviews:
    	$isbns = $this->getISBNs();
    	
    	// Do we have external reviews? --HeBIS
    	if (!empty($isbns)) {
    		$reviews = $this->getReviews();
    		if (!empty($reviews)) {
    		    return true;	
    		}
    	}

    	return false;
    }
    
    /**
     * Does this record have a Table of Contents available? --HeBIS
     *
     * @return bool
     * @access public
     */
    public function hasExternalTOC()
    {
       	// If we have ISBN(s), we might have table of contents:
    	$isbns = $this->getISBNs();
    	
    	// Do we have external TOC? --HeBIS
    	if (!empty($isbns)) {
    		$TOC = $this->getExternalTOC();
    		if (!empty($TOC)) {
    		    return true;	
    		}
    	}

    	return false;
    }        
    
    /**
     * Does this record have a summary available? --HeBIS
     *
     * @return bool
     * @access public
     */
    public function hasSummary()
    {
    	// If we have ISBN(s), we might have reviews:
    	$isbns = $this->getISBNs();
    	
    	// Do we have external summaries? --HeBIS
    	if (!empty($isbns)) {
    		$summaries = $this->getSummaries();
    		if (!empty($summaries)) {
    		    return true;	

    		}
    	}

    	return false;
    }    
    
    /**
     * Check Libreka preview for this record --HeBIS
     *
     * @return bool
     * @access public
     */
    public function hasLIBPreview()
    {
    	$isbn = $this->getCleanISBN();
    	$url = 'http://bookview.libreka.de/retailer/urlResolver.do?id=' . $isbn;
    	
    	// Content to find on the website
    	$content = 'Es tut uns leid';
    	 
    	if (!empty($isbn)) {
    		$response = file_get_contents($url);
    		
    		if (!strpos($response, $content)) {
    			return true;
    		} 		
    	}
    	return false;
    }
    	
    /**
     * Return an associative array of copies of the item with specific information      
     *
     * @return array
     * @access protected
     */
/**
	 * Return an associative array of copies of the item with specific information
	 *
	 * @return array
	 * @access protected
	 */
	protected function getCopies()
	{
		global $configArray;
		$retVal = array();
		 
		// Für Exemplardaten Epn und ILN falls eingestellt
		$exp = $this->picaarray->getEpn();
		if (isset($configArray['HeBIS']['ILN']))
			$exp = $exp .' '.$configArray['HeBIS']['ILN'];
		$tmp = $this->picaarray->getExp();

		foreach ($tmp as $key1 => $value1)
		{
			if (strpos($key1, $exp)>-1)
			{
				$komarray = array();
				$bemarray = array();
				$klassarray = array();
				$verlauf = array();
				$buchnr = '';
				$epn = '';
				$retrourl = '';
				$retrotitel = '';

				foreach ($value1 as $key2 => $value2)
				{
					// EPN
					if (strpos($key2, '203@') === 0)
						$epn = $value2['$0'];
						
					// Klassifikation
					if (strpos($key2, '245Z') === 0)
						$klassarray[] = $value2['$a'];
					
					// Buchnummer
					if (strpos($key2, '209G') === 0 && strpos($value2['$x'], '00') === 0)
						$buchnr = $value2['$a'];
					
					
					// Bemerkung
					if (strpos($key2, '247D') === 0)
						$bemarray[] = $value2['$a'];
					if (strpos($key2, '237A') === 0)
						$bemarray[] = $value2['$a'];
					 
					// Bestand
					if (strpos($key2, '209E') === 0 && strpos($value2['$x'], '01') === 0)
						$verlauf[substr($key2, 4, 2)]['01'] = $value2['$a'];
					if (strpos($key2, '209E') === 0 && strpos($value2['$x'], '02') === 0)
						$verlauf[substr($key2, 4, 2)]['02'] = $value2['$a'];
					if (strpos($key2, '209E') === 0 && strpos($value2['$x'], '03') === 0)
						$verlauf[substr($key2, 4, 2)]['03'] = $value2['$a'];

					// Kommentar
					if (strpos($key2, '209E') === 0 && strpos($value2['$x'], '04') === 0)
						$komarray[] = $value2['$a'];
					
					// Retro URL
					if (strpos($key2, '209U') === 0) {
						$retrourl = $value2['$u'];
						$retrotitel = $value2['$3'];
					}
						
				}
				if (strlen($epn)>0){
					$tmparray = array('kommentare' => $komarray,
							'bemerkungen'=> $bemarray,
							'klassifikationen'=> $klassarray,
							'verlauf'=> $verlauf,
							'buchnr' => $buchnr,
							'retrourl' => $retrourl,
					        'retrotitel' => $retrotitel);
					$retVal[$epn] = $tmparray;
				}
				$komarray = array();
				$bemarray = array();
				$klassarray = array();
				$verlauf = array();
				$epn = '';

			}
		}
		return $retVal;
	}	
    	
    /**
     * Return an associative array of URLs associated with this record (key = URL,
     * value = description).
     *
     * @return array
     * @access protected
     */
    protected function getURLs()
    {
    	global $configArray;
    	$retVal = array();
    	
    	// Für Exemplardate Epn und ILN falls eingestellt
    	$epn = $this->picaarray->getEpn();
    	if (isset($configArray['HeBIS']['ILN']))
    	   $epn = $epn .' '.$configArray['HeBIS']['ILN'];	
        // URL vorläufig aus Pica daten holen
    	$tmp = $this->picaarray->getExp();
    	foreach ($tmp as $key1 => $value1)
    	{
    		if (strpos($key1, $epn)>-1)
    			{
    			foreach ($value1 as $key2 => $value2)
    				{
    					
    				// DOI
    				if (strpos($key2, '204P') === 0)
    	       			$retVal['http://dx.doi.org/'.$value2['$0']] = 'Volltext DOI';

    				// Handle
    				if (strpos($key2, '204R') === 0)
    					$retVal['http://hdl.handle.net/'.$value2['$0']] = 'Volltext Handle';
    		
    				// URN
    				if (strpos($key2, '204U') === 0)
    					$retVal['http://nbn-resolving.de/urn/resolver.pl?urn='.$value2['$0']] = 'Volltext URN';
    				
    				// für die ILN/Abt. gültige Volltext-URL 
    				if (strpos($key2, '209S') === 0)
    					$retVal[$value2['$u']] = 'Volltext';
    				}
    			}
    		}	
    	return $retVal;
    }
    
    /**
     * Assign necessary Smarty variables and return a template name for the current
     * view to load in order to display a summary of the item suitable for use in
     * search results.
     *
     * @param string $view The current view.
     * 
     * @return string      Name of Smarty template file to display.
     * @access public
     */
    public function getSearchResult($view = 'list')
    {
        global $interface;

        // MARC results work just like index results, except that we want to
        // enable the AJAX status display since we assume that MARC records
        // come from the ILS:
        $template = parent::getSearchResult($view);
        $interface->assign('summTitle', $this->getTitle2());
        $interface->assign('summTitleSection', $this->getTitleSection());
        $interface->assign('summTitleSectionType', $this->getTitleSectionType());
        $interface->assign('summPartAuthors', $this->getPartAuthors());
        $interface->assign('summCorporation', $this->getCorporation());
        
        $interface->assign('summJournal', $this->getJournal());
        $interface->assign('summVolumes', $this->getVolumes());
        $interface->assign('summAjaxStatus', true);
        $interface->assign('summEdition', $this->getEdition());
        $interface->assign('summPretitle', $this->getPretitle());
        $interface->assign('summShortSubtitle', $this->getShortSubtitle());
        $interface->assign('summDate', $this->getPlacesOfPublication());
        
        
        // Verfügbarkeitsanzeige
        $tmp = $this->picaarray->getTit();
        foreach ($this->getFormats() as $key => $value)
        {
        	if ($value === 'series' || $value === 'kit' || $value === 'journal' || $value === 'article' || strpos($tmp['002@']['$0'], "rax") === 0)
        		$interface->assign('summAjaxStatus', false);
        }
        
        return $template;
    }
    
    /**
     * Titelanreicherung Vorerst über Pica Felder
     * 
     * Format:
     * array(
     *        array(
     *               'title' => label_for_title
     *               'value' => link_name
     *               'link'  => link_URI
     *        ),
     *        ...
     * )
     *
     * @return null|array
     * @access protected
     */
    protected function getAllTitleLinks()
    {
    	$result = array();
    	$tmp = $this->picaarray->getTit();
    	$suchmuster1 = '.+?';
    	$ersetzung = '';
    	
    	foreach ($tmp as $key=>$value){
    		if (strpos($key, '009P') === 0) {
    			
    			$title = 'Hinweise zum Inhalt';
    			if (array_key_exists('$3', $value)){
    				$title = $value['$3'];
    			}
    			$url = 'xxx';
    			if (array_key_exists('$u', $value)){
    				$url = $value['$u'];
    			}
    			
    			$display = $url;
    			if (strlen($display) > 30 ) {
    				$display = '...'.substr($display, -27);
    			}
    				
    			$tmp2 = array(
    					'title' => $title,
    					'value' => $display,
    					'link'  => $url
    			);
    			
    			$result[] = $tmp2;
                  
    		}
    	}
    	return $result;
    
    }
    
    /**
     * Get the subtitle of the record.
     *
     * @return string
     * @access protected
     */
    protected function getSubtitle()
    {
        // 245 $b_/_$c
    	$tmp = ''; 
    	$tmp1 = $this->_getFieldArray('245', array('h'), false);
    	if (count($tmp1)> 0){
    	   $tmp = $tmp1[0];
    	}
    	
    	$tmp2 = $this->_getFieldArray('245', array('b'), false);
        if (count($tmp2)> 0){
    		if (strlen($tmp)> 0){
    			$tmp = $tmp.' : ';
    		}
    		$tmp = $tmp.$tmp2[0];
    	}
    	
    	$tmp3 = $this->_getFieldArray('245', array('c'), false);
    	if (count($tmp3)> 0){
    		if (strlen($tmp)> 0){
    			$tmp = $tmp.' / ';
    		}
    		$tmp = $tmp.$tmp3[0];
    	}
    	
    	// Sortierzeichen weg
    	if (strpos($tmp, '@') !== false){
    		$occurrence = strpos($tmp, '@');
    		$tmp = substr_replace($tmp, '', $occurrence, 1);
    	}
    	
    	return $tmp;
    }
    
    /**
     * Get the RVK of the record.
     *
     * @return array
     * @access protected
     */
    protected function getShortSubtitle()
    {
    	// 245 $b
    	$tmp = '';
    	$tmp1 = $this->_getFieldArray('245', array('b'), false);
    	if (count($tmp1)> 0){
    		$tmp = $tmp1[0];
    	}
    
    	// Sortierzeichen weg
    	if (strpos($tmp, '@') !== false){
    		$occurrence = strpos($tmp, '@');
    		$tmp = substr_replace($tmp, '', $occurrence, 1);
    	}
        return $tmp;
    }
    
    /**
     * Get the Einheitssachtitel of the record.
     *
     * @return string
     * @access protected
     */
    protected function getRVK()
    {
    	$tmp = array();
    	$fields = $this->marcRecord->getFields('084');
    	foreach ($fields as $currentField) {
    		$allSubfields = $currentField->getSubfields();
    		$a ='';
    		$s2 ='';
    		foreach ($allSubfields as $currentSubfield) {
    			$code = trim($currentSubfield->getCode());
    			$data = trim($currentSubfield->getData());
    			
    			if (strcmp($code, '2')===0){
    				$s2 = $data;
    			}
    			if (strcmp($code, 'a')===0){
    				$a = $data;
    			}
    			if (strcmp($s2, 'rvk')===0 && strlen($a)>0){
    				$tmp[]=$a;
    			}
    			
    		}
    		$a='';
    		$s2='';
    	}
    	return $tmp;
    }
    
    /**
     * Get the Einheitssachtitel of the record.
     *
     * @return string
     * @access protected
     */
    protected function getThesis()
    {
    	return $this->_getFieldArray('502', array('a'));
    }
    
    /**
     * Get the Einheitssachtitel of the record.
     *
     * @return string
     * @access protected
     */
    protected function getPretitle()
    {
    	
    // entweder 246 $a 
    $fields = $this->marcRecord->getFields('246');
    $tmp = '';
    foreach ($fields as $field){
    	if (strcmp($field->getIndicator(2), '9')===0) {
    	   $allSubfields = $field->getSubfields();
    		 foreach ($allSubfields as $currentSubfield) {
    				$code = trim($currentSubfield->getCode());
    				$data = trim($currentSubfield->getData());
    				if (strcmp($code, 'a')===0) {
    					$tmp = $data;
    				}
    	   }
      }    
    }
    if ($tmp === '') {
      // oder 240 $a <$g>
      $tmp1 = $this->_getFieldArray('240', array('a'), false);
    	if (count($tmp1)> 0){
    		$tmp = $tmp1[0];
    	}
    
    	$tmp2 = $this->_getFieldArray('240', array('g'), false);
    	if (count($tmp2)> 0){
    		$tmp = $tmp.' <'.$tmp2[0].'>';
    	}
    }	
    	// Sortierzeichen weg
    	if (strpos($tmp, '@') !== false){
    		$occurrence = strpos($tmp, '@');
    		$tmp = substr_replace($tmp, '', $occurrence, 1);
    	}
    		
    	return $tmp;
    }
    
    /**
     * Get the Einheitssachtitel of the record.
     *
     * @return string
     * @access protected
     */
    protected function getPretitle2()
    {
    	//240 $a <$g>
    	$tmp = '';
    	$tmp1 = $this->_getFieldArray('730', array('a'), false);
    	if (count($tmp1)> 0){
    		$tmp = $tmp1[0];
    	}
    
    	$tmp2 = $this->_getFieldArray('730', array('g'), false);
    	if (count($tmp2)> 0){
    		$tmp = $tmp.' <'.$tmp2[0].'>';
    	}
    
    	// Sortierzeichen weg
    	if (strpos($tmp, '@') !== false){
    		$occurrence = strpos($tmp, '@');
    		$tmp = substr_replace($tmp, '', $occurrence, 1);
    	}
    
    	return $tmp;
    }
    
    /**
     * Get the text of the part/section portion of the title.
     *
     * @return string
     * @access protected
     */
    protected function getTitleSection()
    {
    	// .. Auf neuer Zeile: $n._$p
    	$tmp = array();
    	$fields = $this->marcRecord->getFields('245');

    	// Extract all the requested subfields, if applicable.
    	foreach ($fields as $currentField) {
    		$allSubfields = $currentField->getSubfields();
    		$n = '';
    		$p = '';
    		$paar = array();
    		foreach ($allSubfields as $currentSubfield) {
    			$code = trim($currentSubfield->getCode());
    			$data = trim($currentSubfield->getData());
    			$data = str_replace('[...]', '', $data);
    			if (strpos($data, '@') !== false){
    				$occurrence = strpos($data, '@');
    				$data = substr_replace($data, '', $occurrence, 1);
    			}
    			if (strcmp($code, 'n')===0 && (strlen($n)>0 || strlen($p)>0)){
    				$paar['n']=$n;
    				$paar['p']=$p;
    				$tmp[]=$paar;
    				$n='';
    				$p='';
    				$paar=array();
    			}

    			if (strcmp($code, 'p')===0 && strlen($p)>0){
    				$paar['n']=$n;
    				$paar['p']=$p;
    				$tmp[]=$paar;
    				$n='';
    				$p='';
    				$paar=array();
    			}

    			if (strcmp ($code, 'n') === 0){
    				$n = $data;
    			}
    			else if (strcmp ($code, 'p') === 0){
    				$p = $data;
    			}
    		}
    		// letzten Record anhängen an Ausgabearray()
    		$paar['n']=$n;
    		$paar['p']=$p;
    		$tmp[]=$paar;
    	}
    	return $tmp;
    }
    
    /**
     * Get type of the part/section portion of the title.
     *
     * @return string
     * @access protected
     */
    protected function getTitleSectionType()
       {
       $leader = $this->marcRecord->getLeader();
       
       if ($leader[19] === 'c') {
       	 return 'Teil';
       }
       else {
       	 return 'Unterreihe';
       }
       }
    
    
    /**
     * Get an array of all secondary authors (complementing getPrimaryAuthor()).
     *
     * @return array
     * @access protected
     */
    protected function getSecondaryAuthors()
    {
    	$tmp = array();
    	$tmp2 = '';
    	$fields = $this->marcRecord->getFields('700');
    	
    	// Extract all the requested subfields, if applicable.
    	foreach ($fields as $currentField) {
    	   $allSubfields = $currentField->getSubfields();
    	   $a ='';
    	   $b ='';
    	   $c ='';
    	   $s4 ='';
    	   
    	   foreach ($allSubfields as $currentSubfield) {
    	      $code = trim($currentSubfield->getCode());
    	      $data = trim($currentSubfield->getData());
    	      if (strcmp($code, 'a')===0) {
    	      	$a=$data;
    	      }
    	      if (strcmp($code, 'b')===0) {
    	      	$b=$data;
    	      }
    	      if (strcmp($code, 'c')===0) {
    	      	$c=$data;
    	      }
    	      if (strcmp($code, '4')===0) {
    	      	$s4=$data;
    	      }
    	   }   
    	   if (strcmp($s4, 'aut')===0){	
    	      $tmp2 = $a;
    	
    	      if(strlen($b)>0){
    	         $tmp2 = $tmp2." ".$b;
    	      }
    	      if(strlen($c)>0){
    		      $tmp2 = $tmp2." <".$c.">";
    	      }
    	      $tmp[]=$tmp2;   
    	   }
    	   $a ='';
    	   $b ='';
    	   $c ='';
    	   $s4 ='';
    	   $tmp2='';
    	}
    	return $tmp;
    }
    
    /**
     * Get the main corporate author (if any) for the record.
     *
     * @return string
     * @access protected
     */
    protected function getPartAuthors()
    {
    	$tmp = array();
    	$tmp2 = array();
    	$tmp3='';
    	$fields = $this->marcRecord->getFields('700');
    	
    	// Extract all the requested subfields, if applicable.
    	foreach ($fields as $currentField) {
    	   $allSubfields = $currentField->getSubfields();
    	   $a ='';
    	   $b ='';
    	   $c ='';
    	   $e ='';
    	   $s4 =array();
    	   
    	   foreach ($allSubfields as $currentSubfield) {
    	      $code = trim($currentSubfield->getCode());
    	      $data = trim($currentSubfield->getData());
    	      if (strcmp($code, 'a')===0) {
    	      	$a=$data;
    	      }
    	      if (strcmp($code, 'b')===0) {
    	      	$b=$data;
    	      }
    	      if (strcmp($code, 'c')===0) {
    	      	$c=$data;
    	      }
    	      if (strcmp($code, 'e')===0) {
    	      	$e=$data;
    	      }
    	      if (strcmp($code, '4')===0) {
    	      	$s4[]=$data;
    	      }
    	      
    	   }
    	   if (!(in_array('aut', $s4) || in_array('hnr', $s4) || in_array('prf', $s4))){	
    	      $tmp3 = $a;
    	      
    	      if(strlen($b)>0){
    	         $tmp3 = $tmp3." ".$b;
    	      }
    	      if(strlen($c)>0){
    		      $tmp3 = $tmp3." <".$c.">";
    	      }
    	      
    	      $tmp2['a'] = $tmp3;
    	      if(strlen($e)>0){
    	      	$tmp2['e'] = $e;
    	      }
    	      
    	      $tmp[]=$tmp2;   
    	   }
    	   $a ='';
    	   $b ='';
    	   $c ='';
    	   $s4 =array();
    	   $tmp2=array();
    	}
    	return $tmp;
    }
    
    /**
     * Get the festschrift (if any) for the record.
     *
     * @return string
     * @access protected
     */
    protected function getFestschrift()
    {
    	$tmp = array();
    	$tmp2='';
    	$fields = $this->marcRecord->getFields('700');
    	
    	// Extract all the requested subfields, if applicable.
    	foreach ($fields as $currentField) {
    	   $allSubfields = $currentField->getSubfields();
    	   $a ='';
    	   $b ='';
    	   $c ='';
    	   $e ='';
    	   $s4 =array();
    	   
    	   foreach ($allSubfields as $currentSubfield) {
    	      $code = trim($currentSubfield->getCode());
    	      $data = trim($currentSubfield->getData());
    	      if (strcmp($code, 'a')===0) {
    	      	$a=$data;
    	      }
    	      if (strcmp($code, 'b')===0) {
    	      	$b=$data;
    	      }
    	      if (strcmp($code, 'c')===0) {
    	      	$c=$data;
    	      }
    	      if (strcmp($code, 'e')===0) {
    	      	$e=$data;
    	      }
    	      if (strcmp($code, '4')===0) {
    	      	$s4[]=$data;
    	      }
    	      
    	   }   
    	   if (in_array('hnr', $s4)){	
    	      $tmp2 = $a;
    	      
    	      if(strlen($b)>0){
    	         $tmp2 = $tmp2." ".$b;
    	      }
    	      if(strlen($c)>0){
    		      $tmp2 = $tmp2." <".$c.">";
    	      }
    	      
    	   $tmp[]=$tmp2;   
    	   }
    	   $a ='';
    	   $b ='';
    	   $c ='';
    	   $s4 ='';
    	   $tmp2='';
    	}
    	return $tmp;
    }
    
    /**
     * Get the main corporation (if any) for the record.
     *
     * @return string
     * @access protected
     */
    protected function getCorporation()
    {
    	$fieldnbrs = array('110', '111', '710', '711');
    	$tmp = array();
    	$tmp1 = '';
    	$x2='';
    	

    	foreach($fieldnbrs as $fieldnr){
    		 
    		$fields = $this->marcRecord->getFields($fieldnr);
    		 
    		// Extract all the requested subfields, if applicable.
    		foreach ($fields as $currentField) {
    			$allSubfields = $currentField->getSubfields();

    			foreach ($allSubfields as $currentSubfield) {
    				$code = trim($currentSubfield->getCode());
    				$data = trim($currentSubfield->getData());
    				 
    				// 110, 710
    				if (strcmp($fieldnr, '110')===0 || strcmp($fieldnr, '710')===0){
    					if (strcmp($code, 'a')===0) {
    						$tmp1 = $tmp1.$data;
    					}
    					if (strcmp($code, 'b')===0) {
    						$tmp1 = $tmp1.' / '.$data;
    					}
    					if (strcmp($code, 'g')===0) {
    						$tmp1 = $tmp1.' <'.$data.'>';
    					}
    					if (strcmp($code, 'n')===0) {
    						$tmp1 = $tmp1.' <'.$data.'>';
    					}
    				}
    				// 111, 711
    				if (strcmp($fieldnr, '111')===0 || strcmp($fieldnr, '711')===0){
    					if (strcmp($code, 'a')===0) {
    						$tmp1 = $data;
    					}
    					if (strcmp($code, 'b')===0) {
    						$x2 = $x2.', '.$data;
    					}
    					if (strcmp($code, 'g')===0) {
    						$x2 = $x2.', '.$data;
    					}
    					if (strcmp($code, 'n')===0) {
    						$x2 = $x2.', '.$data;
    					}
    				}
    			}
    			if (strcmp($fieldnr, '111')===0 || strcmp($fieldnr, '711')===0){
    				if (strlen($x2 > 1)){
    					$tmp1 = $tmp1.' <'.substr($x2, 1).'>';
    				}
    			}
    			
    			$tmp[]=$tmp1;
    			$tmp1='';
    			$x2='';
    		}
    	}
    return $tmp;	
    }
    
    /**
     * Get contained media
     *
     * @return string
     * @access protected
     */
    protected function getContained()
    {
    	$tmp = array();
    
    	$tmp1 = $this->_getFieldArray('249', array('a'), false);
    	if (count($tmp1)> 0){
    		if (strpos($tmp1[0], '@') !== false){
    			$occurrence = strpos($tmp1[0], '@');
    			$tmp1[0] = substr_replace($tmp1[0], '', $occurrence, 1);
    		}
    		$tmp[0] = $tmp1[0];
    	}
    
    	$tmp2 = $this->_getFieldArray('249', array('b'), false);
    	if (count($tmp2)> 0){
    		if (strpos($tmp2[0], '@') !== false){
    			$occurrence = strpos($tmp2[0], '@');
    			$tmp2[0] = substr_replace($tmp2[0], '', $occurrence, 1);
    		}
    		$tmp[1] = $tmp2[0];
    	}
    
    	return $tmp;
    }
    
    /**
     * Get the interpreter (if any) for the record.
     *
     * @return string
     * @access protected
     */
    protected function getInterpreter()
    {
    	$fieldnbrs = array('700', '710');
    	$tmp = array();
    	$tmp700 = array();
    	$tmp710 = array();
    	$tmp1 = '';
    	$s4=array();
    	

    	foreach($fieldnbrs as $fieldnr){
    		 
    		$fields = $this->marcRecord->getFields($fieldnr);
    		 
    		// Extract all the requested subfields, if applicable.
    		foreach ($fields as $currentField) {
    			$allSubfields = $currentField->getSubfields();

    			foreach ($allSubfields as $currentSubfield) {
    				$code = trim($currentSubfield->getCode());
    				$data = trim($currentSubfield->getData());
    				 
    				// 700
    				if (strcmp($fieldnr, '700')===0 ){
    					if (strcmp($code, 'a')===0) {
    						$tmp1 = $tmp1.$data;
    					}
    					if (strcmp($code, 'b')===0) {
    						$tmp1 = $tmp1.' '.$data;
    					}
    					if (strcmp($code, 'c')===0) {
    						$tmp1 = $tmp1.' <'.$data.'>';
    					}
    					if (strcmp($code, 'e')===0) {
    						$tmp1 = $tmp1.' ('.$data.')';
    					}
    					if (strcmp($code, '4')===0) {
    						$s4[] = $data;
    					}
    				}
    				// 710
    				if (strcmp($fieldnr, '710')===0 ){
    					if (strcmp($code, 'a')===0) {
    						$tmp1 = $data;
    					}
    					if (strcmp($code, 'b')===0) {
    						$tmp1 = $tmp1.' / '.$data;
    					}
    					if (strcmp($code, 'g')===0) {
    						$tmp1 = $tmp1.' <'.$data.'>';
    					}
    					if (strcmp($code, 'n')===0) {
    						$tmp1 = $tmp1.' <'.$data.'>';
    					}
    					if (strcmp($code, '4')===0) {
    						$s4[] = $data;
    					}
    				}
    			}
    			if (strcmp($fieldnr, '700')===0 && in_array('prf', $s4)){
    			  $tmp700[]=$tmp1;
    				}
    			elseif (strcmp($fieldnr, '710')===0 && in_array('mus', $s4)){
    			  $tmp710[]=$tmp1;
    				}	
    			
    			$tmp1='';
    			$s4=array();
    		}
    	}
    $tmp[0]=$tmp700;
    $tmp[1]=$tmp710;
    return $tmp;	
    }
    
    /**
     * Get an array of all secondary authors (complementing getPrimaryAuthor()).
     *
     * @return array
     * @access protected
     */
    protected function getRetroUrl()
    {
    	// preliminary solution for detection of series
    	$tmp = $this->picaarray->getTit();
    	if (array_key_exists ('009R', $tmp) && strlen($tmp['009R']['$u'])>0 && array_key_exists ('002@', $tmp) && strpos($tmp['002@']['$0'], "r") === 0){
    		return array($tmp['009R']['$u'], $tmp['009R']['$3']);
    	}	
    	return null;
    }
    
    /**
     * Assign necessary Smarty variables and return a template name to
     * load in order to display holdings extracted from the base record
     * (i.e. URLs in MARC 856 fields) and, if necessary, the ILS driver.
     * Returns null if no data is available.
     *
     * @param array $patron An array of patron data
     *
     * @return string Name of Smarty template file to display.
     * @access public
     */
    public function getHoldings($patron = false)
    {
    	global $interface;
    	
    	$template = parent::getHoldings($patron);
    	
    	$interface->assign('holdingRetroURL', $this->getRetroUrl());
    	if (in_array('series', $this->getFormats())){
           $interface->assign('holdingSeries', true);
    	}
    	return $template;
    }
    
    /**
     * Assign necessary Smarty variables and return a template name to
     * load in order to display a summary of the item suitable for use in
     * user's favorites list.
     *
     * @param object $user      User object owning tag/note metadata.
     * @param int    $listId    ID of list containing desired tags/notes (or null
     * to show tags/notes from all user's lists).
     * @param bool   $allowEdit Should we display edit controls?
     *
     * @return string           Name of Smarty template file to display.
     * @access public
     */
    public function getListEntry($user, $listId = null, $allowEdit = true)
    {
    	global $interface;
    
    	// Extract bibliographic metadata from the record:
    	$id = $this->getUniqueID();
    	$interface->assign('listId', $id);
    	$interface->assign('listFormats', $this->getFormats());
    	$interface->assign('listTitle', $this->getTitle());
    	$interface->assign('listAuthor', $this->getPrimaryAuthor());
    	$interface->assign('listThumb', $this->getThumbnail());
    
    	// Extract user metadata from the database:
    	$notes = array();
    	$data = $user->getSavedData($id, $listId);
    	foreach ($data as $current) {
    		if (!empty($current->notes)) {
    			$notes[] = $current->notes;
    		}
    	}
    	$interface->assign('listNotes', $notes);
    	$interface->assign('listTags', $user->getTags($id, $listId));
    
    	// Pass some parameters along to the template to influence edit controls:
    	$interface->assign('listSelected', $listId);
    	$interface->assign('listEditAllowed', $allowEdit);
    
    	$notpumafavorites[]=array(
    			'listId' => $id,
    			'listFormats' => $this->getFormats(),
    			'listTitle' => $this->getTitle(),
    			'listAuthor' => $this->getPrimaryAuthor(),
    			'listThumb' => $this->getThumbnail(),
    			'listNotes' => $notes,
    			'listTags' => $user->getTags($id, $listId),
    			'listSelected' => $listId,
    			'listEditAllowed' => $allowEdit
    	);
    
    	return $notpumafavorites;
    }
    
    /**
     * Get all subject headings associated with this record.  Each heading is
     * returned as an array of chunks, increasing from least specific to most
     * specific.
     *
     * @return array
     * @access protected
     */
    protected function getReportNumber()
    {
    	// Try each MARC field one at a time:
    	// 088
    	return $this->_getFieldSubfieldArray('088', array('a'));
    	}
    	
    	protected function getAnnotation(){
    		
    		// einfache Marc Felder
    		$tmp = $this->_getFieldSubfieldArray('501', array('a'));
    		$tmp = array_merge($tmp, $this->_getFieldSubfieldArray('546', array('a')));
    		$tmp = array_merge($tmp, $this->_getFieldSubfieldArray('500', array('a')));
    		$tmp = array_merge($tmp, $this->_getFieldSubfieldArray('515', array('a')));
    		$tmp = array_merge($tmp, $this->_getFieldSubfieldArray('518', array('a')));
    		$tmp = array_merge($tmp, $this->_getFieldSubfieldArray('538', array('a')));
    		$tmp = array_merge($tmp, $this->_getFieldSubfieldArray('550', array('a')));
    		$tmp = array_merge($tmp, $this->_getFieldSubfieldArray('555', array('a')));
    		$tmp = array_merge($tmp, $this->_getFieldSubfieldArray('583', array('a')));

    		// Marc Felder mit mehreren Bedingungen
    		$fields = $this->marcRecord->getFields('246');
    		$a = '';
    		$i = '';
    		foreach ($fields as $field){
    			if (strcmp($field->getIndicator(1), '1')===0 ||
    					strcmp($field->getIndicator(1), '0')===0){
    				$allSubfields = $field->getSubfields();
    				foreach ($allSubfields as $currentSubfield) {
    					$code = trim($currentSubfield->getCode());
    					$data = trim($currentSubfield->getData());
    					if (strcmp($code, 'i')===0) {
    						$i = $data;
    					}
    					if (strcmp($code, 'a')===0) {
    						$a = $data;
    					}
    				}
    				If (strlen($i)>0 && strlen($a)>0){
    					$tmp[]=array(array('a', $a));
    					$a = '';
    					$i = '';
    				}
    			}
    		}
    		$fields = $this->marcRecord->getFields('247');
    		foreach ($fields as $field){
    			if (strcmp($field->getIndicator(2), '0')===0){
    				$allSubfields = $field->getSubfields();
    				foreach ($allSubfields as $currentSubfield) {
    					$code = trim($currentSubfield->getCode());
    					$data = trim($currentSubfield->getData());
    					if (strcmp($code, 'a')===0) {
    						$tmp[]=array(array('a', $a));
    					}
    				}
    			}
    		}
    		
    		// Pica Felder
    		$tmp1 = $this->picaarray->getTit();
    		foreach ($tmp1 as $key=>$value){
    			if (strpos($key, '046A') === 0 || strpos($key, '046C') === 0 || 
    				strpos($key, '046K') === 0 || strpos($key, '046M') === 0) {
    				if (array_key_exists ('$a', $value)){
    					$tmp[] = array(array('a', $value['$a']));
    				}
    			}
    		}
            
    		return $tmp;

    	}
    	
    /**
     * Get all subject headings associated with this record.  Each heading is
     * returned as an array of chunks, increasing from least specific to most
     * specific.
     *
     * @return array
     * @access protected
     */
    protected function getAllSubjectHeadings()
    {
    	// This is all the collected data:
    	$retval = array();
    	$tmp = array();
    	$ppn = '';
    	$display = '';
    	$search = '';
    	
    	// Try each MARC field one at a time:
    	// 600
    	$tmp = $this->_getFieldSubfieldArray('600', array('0', 'a', 'b', 'c', 't', 'x'));
    	if (count($tmp)>0){
    		foreach ($tmp as $field){
    			foreach ($field as $subfield){
    				if (strcmp($subfield[0], 'a')===0) {
    					$display = $display.$subfield[1];
    					$search = $search.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'b')===0) {
    					$display = $display.' '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'c')===0) {
    					$display = $display.' <'.$subfield[1].'>';
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 't')===0) {
    					$display = $display.' / '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'x')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], '0')===0){
    					if (strpos($subfield[1], '(DE-603)')!== false){
    						$ppn = str_replace("(DE-603)", "", $subfield[1]);
    					}
    				}
    			}
    			 

    			$retval['600'][] = array('ppn'=>$ppn, 'display'=>$display, 'search'=>$search);
    			$ppn = '';
    			$display='';
    			$search='';
    		}
    	}
    	
    	// 610
    	$tmp = $this->_getFieldSubfieldArray('610', array('0', 'a', 'b', 'g', 't', 'x'));
    	if (count($tmp)>0){
    		foreach ($tmp as $field){
    			foreach ($field as $subfield){
    				if (strcmp($subfield[0], 'a')===0) {
    					$display = $display.$subfield[1];
    					$search = $search.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'b')===0) {
    					$display = $display.' / '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'g')===0) {
    					$display = $display.' <'.$subfield[1].'>';
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 't')===0) {
    					$display = $display.' / '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'x')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], '0')===0){
    					if (strpos($subfield[1], '(DE-603)')!== false){
    						$ppn = str_replace("(DE-603)", "", $subfield[1]);
    					}
    				}
    			}
    			$retval['610'][] = array('ppn'=>$ppn, 'display'=>$display, 'search'=>$search);
    			$ppn = '';
    			$display='';
    			$search='';
    		}
    	}
    	
    	// 611
    	$tmp = $this->_getFieldSubfieldArray('611', array('0', 'a', 'c', 'd', 'e', 'f', 'g', 'n', 't', 'x'));
    	if (count($tmp)>0){
    		foreach ($tmp as $field){
    			foreach ($field as $subfield){
    				if (strcmp($subfield[0], 'a')===0) {
    					$display = $display.$subfield[1];
    					$search = $search.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'c')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'd')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'e')===0) {
    					$display = $display.' / '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'f')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'g')===0) {
    					$display = $display.' <'.$subfield[1].'>';
    					$search = $search.' '.$subfield[1];
    				}
    			    elseif (strcmp($subfield[0], 'n')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 't')===0) {
    					$display = $display.' / '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'x')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], '0')===0){
    					if (strpos($subfield[1], '(DE-603)')!== false){
    						$ppn = str_replace("(DE-603)", "", $subfield[1]);
    					}
    				}
    			}
    	
    	
    			$retval['611'][] = array('ppn'=>$ppn, 'display'=>$display, 'search'=>$search);
    			$ppn = '';
    			$display='';
    			$search='';
    		}
    	}
    	
    	// 630
    	$tmp = $this->_getFieldSubfieldArray('630', array('0', 'a', 'd', 'e', 'f', 'g', 'n', 't', 'x'));
    	if (count($tmp)>0){
    		foreach ($tmp as $field){
    			foreach ($field as $subfield){
    				if (strcmp($subfield[0], 'a')===0) {
    					$display = $display.$subfield[1];
    					$search = $search.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'd')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'e')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'f')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'g')===0) {
    					$display = $display.' <'.$subfield[1].'>';
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'n')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 't')===0) {
    					$display = $display.' / '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'x')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], '0')===0){
    					if (strpos($subfield[1], '(DE-603)')!== false){
    						$ppn = str_replace("(DE-603)", "", $subfield[1]);
    					}
    				}
    			}
    			$retval['630'][] = array('ppn'=>$ppn, 'display'=>$display, 'search'=>$search);
    			$ppn = '';
    			$display='';
    			$search='';
    		}
    	}
    	
    	// 650
    	$tmp = $this->_getFieldSubfieldArray('650',  array('0', 'a', 'c', 'x', '9'));
    	if (count($tmp)>0){
    		foreach ($tmp as $field){
    			foreach ($field as $subfield){
    				if (strcmp($subfield[0], 'a')===0) {
    					$display = $display.$subfield[1];
    					$search = $search.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'c')===0) {
    					$display = $display.' <'.$subfield[1].'>';
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'x')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], '9')===0) {
    					$display = $display.' <'.str_replace("g:", "", $subfield[1]).'>';
    					$search = $search.' '.str_replace("g:", "", $subfield[1]);
    				}
    				elseif (strcmp($subfield[0], '0')===0){
    					if (strpos($subfield[1], '(DE-603)')!== false){
    						$ppn = str_replace("(DE-603)", "", $subfield[1]);
    					}
    				}
    			}
    			$retval['650'][] = array('ppn'=>$ppn, 'display'=>$display, 'search'=>$search);
    			$ppn = '';
    			$display='';
    			$search='';
    		}
    	}
    	
    	// 651
    	$tmp = $this->_getFieldSubfieldArray('651',  array('0', 'a', 'g', 'x', 'z'));
    	if (count($tmp)>0){
    		foreach ($tmp as $field){
    			foreach ($field as $subfield){
    				if (strcmp($subfield[0], 'a')===0) {
    					$display = $display.$subfield[1];
    					$search = $search.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'g')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'x')===0) {
    					$display = $display.' / '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    			    elseif (strcmp($subfield[0], 'z')===0) {
    					$display = $display.', '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], '0')===0){
    					if (strpos($subfield[1], '(DE-603)')!== false){
    						$ppn = str_replace("(DE-603)", "", $subfield[1]);
    					}
    				}
    			}
    			$retval['651'][] = array('ppn'=>$ppn, 'display'=>$display, 'search'=>$search);
    			$ppn = '';
    			$display='';
    			$search='';
    		}
    	}
    	
    	// 655
    	$tmp = $this->_getFieldSubfieldArray('655',  array('0', 'a', 'z'));
    	if (count($tmp)>0){
    		foreach ($tmp as $field){
    			foreach ($field as $subfield){
    				if (strcmp($subfield[0], 'a')===0) {
    					$display = $display.$subfield[1];
    					$search = $search.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], 'z')===0) {
    					$display = $display.' '.$subfield[1];
    					$search = $search.' '.$subfield[1];
    				}
    				elseif (strcmp($subfield[0], '0')===0){
    					if (strpos($subfield[1], '(DE-603)')!== false){
    						$ppn = str_replace("(DE-603)", "", $subfield[1]);
    					}
    				}
    			}
    			$retval['655'][] = array('ppn'=>$ppn, 'display'=>$display, 'search'=>$search);
    			$ppn = '';
    			$display='';
    			$search='';
    		}
    	}
    	
    	// 648
    	$tmp = $this->_getFieldSubfieldArray('648',  array('0', 'a'));
    	if (count($tmp)>0){
    		foreach ($tmp as $field){
    			foreach ($field as $subfield){
    				if (strcmp($subfield[0], 'a')===0) {
    					$display = $display.$subfield[1];
    					$search = $search.$subfield[1];
    				}
    			}
    			$retval['648'][] = array('ppn'=>$ppn, 'display'=>$display, 'search'=>$search);
    			$ppn = '';
    			$display='';
    			$search='';
    		}
    	}
    
    	return $retval;
    }
    
    /**
     * 
     */
    protected function _getFieldSubfieldArray($field, $subfields)
    {
    	// Default to subfield a if nothing is specified.
    	if (!is_array($subfields)) {
    		return array();
    	}

    	// Initialize return array
    	$matches = array();

    	// Try to look up the specified field, return empty array if it doesn't
    	// exist.
    	$fields = $this->marcRecord->getFields($field);
    	if (!is_array($fields)) {
    		return array();
    	}

    	// Extract all the requested subfields, if applicable.
    	$tmp = array();
    	foreach ($fields as $currentField) {
    		$fsubfields = $currentField->getSubfields();
    		foreach ($fsubfields as $fsubfield) {
    			$code = trim($fsubfield->getCode());
    			$data = trim($fsubfield->getData());
    			if (in_array($code, $subfields)){
    				$tmp[]=array($code, $data);
    			}
    		}
    		if (count($tmp)>0){
    			$matches[] = $tmp;
    			$tmp = array();
    		}
    	}
    	return $matches;
    }
}    

?>        