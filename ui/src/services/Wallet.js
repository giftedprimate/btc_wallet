import bip39 from 'bip39'
import bitcoin from 'bitcoinjs-lib'

class Wallet {
  constructor() {
    this.mnemonic = null
    this.privateKeyWif = null
    this.publicKeyAddress = null

    this.init()
  }

  init () {
    this.createMnemonic(() => {
      this.createPrivateKeyWif(() => {
        this.generatePublicKey()
      })
    })
  }

  createMnemonic (next) {
    const mnemonic = bip39.generateMnemonic()
    const seedValidated = bip39.validateMnemonic(mnemonic)
    if (seedValidated) {
      this.mnemonic = mnemonic
      next()
    } else this.createMnemonic()
  }

  createPrivateKeyWif (next) {
    const seed = bip39.mnemonicToSeed(this.mnemonic)
    this.privateKeyWif = this._generateNewAddress(this._generateRoot(seed), "m/0'/0/0")
    next()
  }

  generatePublicKey () {
    const publicKeyBuffer = new Buffer(this.privateKeyWif, 'hex')
    const publicKeyHash = bitcoin.crypto.hash160(publicKeyBuffer)
    this.publicKeyAddress = bitcoin.address.toBase58Check(publicKeyHash, bitcoin.networks.bitcoin.pubKeyHash)
  }

  _generateRoot (seed) {
    return bitcoin.HDNode.fromSeedBuffer(seed)
  }

  _generateNewAddress (root, path) {
    return root.derivePath(path).getAddress()
  }
}

export default Wallet
