require 'rake'
require 'mirah'

task :compile do
  Mirah.compile('-c', 'lib/swt.jar', '-d', 'classes', 'src/mirah')
end

task :java do
  Mirah.compile('-j', '-c', 'lib/swt.jar', '--cd', 'src/mirah', '-d', '../java', '.')
end

task :default => :compile
