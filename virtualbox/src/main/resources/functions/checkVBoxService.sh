function checkVBoxService {
   local _FOUND=`ps aux | grep '[V]BoxService'`
   [ -n "$_FOUND" ] && {
         return 0
      } || {
         return 1
      }
}