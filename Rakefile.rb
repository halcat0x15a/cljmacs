require 'rake'
require 'rake/testtask'
require 'mirah'

libs = ['lib/swt.jar',
        'lib/swt-gtk-linux-x86-3.5.2.jar',
        'lib/commons-configuration-1.6.jar',
#        'lib/commons-lang3-3.0.1.jar',
        'lib/commons-lang-2.4.jar']

src = 'src/mirah'

classpath = Mirah::Env.encode_paths(libs)

task :compile do
  Mirah.compile('-c', classpath, '--cd', src, '-d', '../../classes', '.')
end

task :java do
  Mirah.compile('-j', '-c', classpath, '--cd', src, '-d', '../java', '.')
end

Rake::TestTask.new do |t|
  $CLASSPATH << 'classes'
  libs.each do |l|
    $CLASSPATH << l
  end
  t.test_files = FileList['test/mirah/cljmacs/test/test*.rb']
end
