# .bashrc

# Source global definitions
if [ -f /etc/bashrc ]; then
	. /etc/bashrc
fi

export PATH=$PATH:.:$HOME/bin:$HOME/bin
export CDPATH=~/:.:~/oss

# User specific aliases and functions

export PS1="\\u:\\w> "

# Alias Definitions
alias ls='ls -F'
#alias rm='rm -i'
alias h=history
alias svnps='/bin/ps -ef |grep svnserve |grep -v grep'

# total disk in use, followed by the top 10 disk hogs, in descending order
alias ducks='du -cks * |sort -rn |head -11'

