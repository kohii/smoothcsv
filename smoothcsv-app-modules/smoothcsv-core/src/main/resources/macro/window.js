module.exports = {
  alert: function(message) {
    javax.swing.JOptionPane.showMessageDialog(null, message);
  },
  confirm: function(message) {
    var result = javax.swing.JOptionPane.showConfirmDialog(null, message, null, javax.swing.JOptionPane.OK_CANCEL_OPTION);
    return result === javax.swing.JOptionPane.OK_OPTION;
  },
  prompt: function(message, value) {
    var result = javax.swing.JOptionPane.showInputDialog(null, message, value == null ? '' : value);
    return result + '';
  }
};