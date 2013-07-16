function nextQualifier(key, qualifier) {
  var ext = parseInt(key) + 1;
  return "ext " + ext + " " + qualifier;
}