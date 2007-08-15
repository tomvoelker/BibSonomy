use strict;

# use Bibliotech::CitationSource::Amazon;
use Bibliotech::CitationSource::Dlib;

use Data::Dumper;

#my $uri = URI->new('http://www.amazon.com/Concrete-Mathematics-Foundation-Computer-Science/dp/0201558025/sr=1-3/qid=1162224711/ref=pd_bbs_3/102-5775907-5020122?ie=UTF8&s=books');
my $uri = URI->new('http://www.dlib.org/dlib/october06/king/10king.html');
print "$uri\n";

my $amazon = new Bibliotech::CitationSource::Dlib;

print $amazon->understands($uri), "\n";

my $results = $amazon->citations($uri), "\n";

for my $result (@$results) {
    print Dumper($result);
#    for my $key (keys %$result) {
#	print $key, "->", $result->{$key}, "\n";
#	if ($key eq "citation") {
#	    for my $key2 (keys %{$result->{$key}}) {
#		print "   $key2->", $result->{$key}{$key2}, "\n";
#		if ($key2 eq "authors") {
#		    print join "\n", @{$result->{$key}{$key2}};
#		}
#	    }
#	}
#    }
}
