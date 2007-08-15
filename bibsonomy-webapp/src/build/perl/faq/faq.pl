use XML::XPath;
use XML::XPath::XMLParser;
use Data::Dumper;

sub normalize ($) {
  my $text = shift;
  $text =~ s/\n/ /gs;
  $text =~ s/\s+/ /g;
  return $text;
}

my $xp = XML::XPath->new(filename => 'faq_base.jsp');
my $root_node = ($xp->find("/")->get_nodelist())[0];

print STDERR "Root gefunden.\n";

my $faqbase = ($xp->find("//ul[\@id=\"faqbase\"]")->get_nodelist())[0];
my $bookbox = ($xp->find("//div[\@id=\"bookbox\"]")->get_nodelist())[0];

my $h3s = $xp->find(q{//div[@class="section"]/h3}, $root_node);

my $sections = [];

foreach my $node ($h3s->get_nodelist()) {
  my $text = normalize($node->string_value());
  my $id = $node->getAttribute("id");

  my $li = XML::XPath::Node::Element->new('li');
  $li->appendChild(XML::XPath::Node::Text->new($text));

  my $ul = XML::XPath::Node::Element->new('ul');
  $li->appendChild($ul);

  $faqbase->appendChild($li);

  my $section = [$text, $id];
  my $questions = [];

  my $dts = $xp->find("../following-sibling::dl[1]/dt", $node);
  my $count = 1;
  foreach my $dt ($dts->get_nodelist()) {
    my $dtid = "$id-$count";
    my $text = normalize($dt->string_value);
    $count++;

    my $inner_li = XML::XPath::Node::Element->new('li');
    $ul->appendChild($inner_li);
    my $a = XML::XPath::Node::Element->new('a');
    $a->appendAttribute(XML::XPath::Node::Attribute->new("href", "#$dtid"));
    $a->appendChild(XML::XPath::Node::Text->new($text));
    $inner_li->appendChild($a);

    my $anchor = XML::XPath::Node::Element->new('a');
    $anchor->appendAttribute(XML::XPath::Node::Attribute->new("name", $dtid));
    $dt->appendChild($anchor);

    push @$questions, [ $text, $dtid ];
  }  

  push @$section, $questions;
  push @$sections, $section;
}

open OUT, ">faq_generated.jsp" or die;
my $text = $root_node->toString();


$text =~ s/^.*(<head)/\1/gs;
$text =~ s/\&lt;/</g;
$text =~ s/shape="rect"//g;
$text =~ s/(<a.*?)\/>/\1><\/a>/g;
print OUT $text;
print "faq_generated.jsp written\n";

open OUT, ">bookbox.jsp" or die;
my $text = $bookbox->toString();

$text =~ s/^.*(<head)/\1/gs;
$text =~ s/\&lt;/</g;
$text =~ s/shape="rect"//g;
$text =~ s/(<a.*?)\/>/\1><\/a>/g;
print OUT $text;
print "bookbox.jsp written\n";

close OUT;

