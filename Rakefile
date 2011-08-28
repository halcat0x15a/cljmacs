require 'rake'
require 'mirah'

task :compile do
  Mirah.compile('-c', 'lib/swt.jar', '-d', 'classes', 'src/mirah')
end

task :default => :compile
